package ru.ssau.codecleaner.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.ssau.codecleaner.entity.*;
import ru.ssau.codecleaner.repository.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class CodeAnalysisService {

    private final FileReportRepository fileReportRepository;
    private final DeadCodeFragmentRepository deadCodeFragmentRepository;

    public CodeAnalysisService(FileReportRepository fileReportRepository,
                               DeadCodeFragmentRepository deadCodeFragmentRepository) {
        this.fileReportRepository = fileReportRepository;
        this.deadCodeFragmentRepository = deadCodeFragmentRepository;
    }

    private int findLineNumberWithSelector(String[] lines, String selector) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(selector)) {
                return i + 1; // номера строк с 1
            }
        }
        return 0;
    }

    private String findSelectorSnippet(String[] lines, String selector, int lineNumber) {
        if (lineNumber == 0) return selector;

        int idx = lineNumber - 1;
        StringBuilder snippet = new StringBuilder();

        // Находим начало блока
        int startIdx = idx;
        while (startIdx > 0 && !lines[startIdx].contains(selector) && !lines[startIdx].contains("{")) {
            startIdx--;
        }

        // Собираем блок
        int braceCount = 0;
        boolean blockStarted = false;

        for (int i = startIdx; i < Math.min(lines.length, startIdx + 30); i++) {
            String line = lines[i];
            snippet.append(line).append("\n");

            if (line.contains("{")) {
                blockStarted = true;
            }

            braceCount += countChar(line, '{');
            braceCount -= countChar(line, '}');

            if (blockStarted && braceCount <= 0 && i > startIdx) {
                break;
            }
        }

        String result = snippet.toString().trim();
        if (result.length() > 500) {
            result = result.substring(0, 500) + "...";
        }
        return result;
    }

    private int findLineNumberWithFunction(String[] lines, String functionName) {
        Pattern pattern = Pattern.compile("function\\s+" + Pattern.quote(functionName));
        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                return i + 1;
            }
        }
        return 0;
    }

    private String findFunctionSnippet(String[] lines, String functionName, int lineNumber) {
        if (lineNumber == 0) return functionName;

        StringBuilder snippet = new StringBuilder();
        int idx = lineNumber - 1;
        int braceCount = 0;
        boolean started = false;

        for (int i = idx; i < Math.min(lines.length, idx + 20); i++) {
            String line = lines[i];
            snippet.append(line).append("\n");

            if (!started && line.contains("function")) {
                started = true;
            }

            braceCount += countChar(line, '{');
            braceCount -= countChar(line, '}');

            if (started && braceCount <= 0) {
                break;
            }
        }

        String result = snippet.toString();
        if (result.length() > 500) {
            result = result.substring(0, 500) + "...";
        }
        return result;
    }

    private int findLineNumberWithVariable(String[] lines, String varName) {
        Pattern pattern = Pattern.compile("(?:const|let|var)\\s+" + Pattern.quote(varName) + "\\s*=");
        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                return i + 1;
            }
        }
        return 0;
    }

    private String findVariableSnippet(String[] lines, String varName, int lineNumber) {
        if (lineNumber == 0) return varName;
        String line = lines[lineNumber - 1].trim();
        if (line.length() > 200) {
            line = line.substring(0, 200) + "...";
        }
        return line;
    }

    private int countChar(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

    private int findBlockEndLine(String[] lines, int startLine) {
        if (startLine == 0) return startLine;

        int idx = startLine - 1;
        int braceCount = 0;
        boolean blockStarted = false;

        for (int i = idx; i < Math.min(lines.length, idx + 30); i++) {
            String line = lines[i];

            if (line.contains("{")) {
                blockStarted = true;
            }

            braceCount += countChar(line, '{');
            braceCount -= countChar(line, '}');

            if (blockStarted && braceCount <= 0 && i > idx) {
                return i + 1;
            }
        }

        return startLine;
    }

    private int findJsBlockEndLine(String[] lines, int startLine) {
        if (startLine == 0) return startLine;

        int idx = startLine - 1;
        int braceCount = 0;
        boolean blockStarted = false;

        for (int i = idx; i < Math.min(lines.length, idx + 50); i++) {
            String line = lines[i];

            if (line.contains("{") && !blockStarted) {
                blockStarted = true;
            }

            braceCount += countChar(line, '{');
            braceCount -= countChar(line, '}');

            if (blockStarted && braceCount <= 0 && i > idx) {
                return i + 1;
            }
        }

        return startLine;
    }

    public AnalysisSession analyzeProject(MultipartFile zipFile, AnalysisSession session) throws IOException {
        // Создаём временную директорию
        Path tempDir = Files.createTempDirectory("codeanalysis_");

        try {
            // 1. Распаковываем ZIP
            unzip(zipFile, tempDir);

            // 2. Собираем все файлы
            List<Path> cssFiles = findFiles(tempDir, ".css");
            List<Path> jsFiles = findFiles(tempDir, ".js");
            List<Path> htmlFiles = findFiles(tempDir, ".html");
            List<Path> allFiles = new ArrayList<>();
            allFiles.addAll(cssFiles);
            allFiles.addAll(jsFiles);
            allFiles.addAll(htmlFiles);

            // 3. Анализируем CSS файлы
            for (Path cssFile : cssFiles) {
                analyzeCssFile(cssFile, htmlFiles, jsFiles, session);
            }

            // 4. Анализируем JS файлы
            for (Path jsFile : jsFiles) {
                analyzeJsFile(jsFile, htmlFiles, jsFiles, session);
            }

            // 5. Вычисляем общую метрику здоровья
            double totalHealth = calculateHealthScore(session);
            session.setHealthScore(totalHealth);

            return session;

        } finally {
            // Удаляем временные файлы
            deleteDirectory(tempDir);
        }
    }

    private void analyzeCssFile(Path cssFile, List<Path> htmlFiles, List<Path> jsFiles,
                                AnalysisSession session) throws IOException {
        String content = Files.readString(cssFile);
        List<String> selectors = extractCssSelectors(content);

        // Собираем весь контент для поиска
        String allContent = getAllContent(htmlFiles, jsFiles);

        // Находим используемые и неиспользуемые селекторы
        List<String> unusedSelectors = new ArrayList<>();

        for (String selector : selectors) {
            if (!allContent.contains(selector)) {
                unusedSelectors.add(selector);
            }
        }

        // Вычисляем размер неиспользуемого кода
        long totalSize = Files.size(cssFile);
        long unusedSize = estimateUnusedSize(content, unusedSelectors);
        double unusedPercentage = (totalSize == 0) ? 0 : (unusedSize * 100.0 / totalSize);

        // Создаём FileReport
        FileReport report = new FileReport();
        report.setAnalysis(session);
        report.setFilePath(cssFile.toString());
        report.setTotalSizeBytes(totalSize);
        report.setUnusedSizeBytes(unusedSize);
        report.setUnusedPercentage(unusedPercentage);
        report.setFileType(FileType.CSS);

        fileReportRepository.save(report);

        // Создаём DeadCodeFragment для каждого неиспользуемого селектора
        String[] lines = content.split("\n");
        for (String selector : unusedSelectors) {
            int lineNumber = findLineNumberWithSelector(lines, selector);
            String snippet = findSelectorSnippet(lines, selector, lineNumber);
            int lineEnd = findBlockEndLine(lines, lineNumber);

            DeadCodeFragment fragment = new DeadCodeFragment();
            fragment.setFileReport(report);
            fragment.setSelectorOrFunction(selector);
            fragment.setCodeSnippet(snippet);
            fragment.setReason("CSS селектор не найден в HTML/JS файлах");
            fragment.setLineStart(lineNumber);
            fragment.setLineEnd(lineEnd);

            deadCodeFragmentRepository.save(fragment);
        }
    }

    private void analyzeJsFile(Path jsFile, List<Path> htmlFiles, List<Path> jsFiles,
                               AnalysisSession session) throws IOException {
        String content = Files.readString(jsFile);
        List<String> functions = extractJsFunctions(content);
        List<String> variables = extractJsVariables(content);

        // Собираем весь контент для поиска
        String allContent = getAllContent(htmlFiles, jsFiles);

        // Находим неиспользуемые функции
        List<String> unusedFunctions = new ArrayList<>();
        for (String func : functions) {
            if (!allContent.contains(func)) {
                unusedFunctions.add(func);
            }
        }

        // Находим неиспользуемые переменные
        List<String> unusedVariables = new ArrayList<>();
        for (String var : variables) {
            int usageCount = countOccurrences(allContent, var);
            if (usageCount <= 1) {
                unusedVariables.add(var);
            }
        }

        long totalSize = Files.size(jsFile);
        long unusedSize = estimateUnusedSize(content, unusedFunctions) +
                estimateUnusedSize(content, unusedVariables);
        double unusedPercentage = (totalSize == 0) ? 0 : (unusedSize * 100.0 / totalSize);

        // Создаём FileReport
        FileReport report = new FileReport();
        report.setAnalysis(session);
        report.setFilePath(jsFile.toString());
        report.setTotalSizeBytes(totalSize);
        report.setUnusedSizeBytes(unusedSize);
        report.setUnusedPercentage(unusedPercentage);
        report.setFileType(FileType.JS);

        fileReportRepository.save(report);

        String[] lines = content.split("\n");

        // Создаём фрагменты для неиспользуемых функций
        for (String func : unusedFunctions) {
            int lineNumber = findLineNumberWithFunction(lines, func);
            String snippet = findFunctionSnippet(lines, func, lineNumber);
            int lineEnd = findJsBlockEndLine(lines, lineNumber);

            DeadCodeFragment fragment = new DeadCodeFragment();
            fragment.setFileReport(report);
            fragment.setSelectorOrFunction(func);
            fragment.setCodeSnippet(snippet);
            fragment.setReason("JS функция не вызывается в проекте");
            fragment.setLineStart(lineNumber);
            fragment.setLineEnd(lineEnd);

            deadCodeFragmentRepository.save(fragment);
        }

        // Создаём фрагменты для неиспользуемых переменных
        for (String var : unusedVariables) {
            int lineNumber = findLineNumberWithVariable(lines, var);
            String snippet = findVariableSnippet(lines, var, lineNumber);

            DeadCodeFragment fragment = new DeadCodeFragment();
            fragment.setFileReport(report);
            fragment.setSelectorOrFunction(var);
            fragment.setCodeSnippet(snippet);
            fragment.setReason("JS переменная объявлена, но не используется");
            fragment.setLineStart(lineNumber);
            fragment.setLineEnd(lineNumber);

            deadCodeFragmentRepository.save(fragment);
        }
    }

    private List<String> extractCssSelectors(String css) {
        Set<String> selectorsSet = new HashSet<>(); // используем Set для уникальности
        // Регулярное выражение для поиска CSS селекторов
        // Ищем текст до {, не включая комментарии и медиа-запросы
        Pattern pattern = Pattern.compile("([^{}]+)\\{");
        Matcher matcher = pattern.matcher(css);

        while (matcher.find()) {
            String selectorPart = matcher.group(1).trim();
            // Разделяем по запятым для множественных селекторов
            String[] parts = selectorPart.split(",");
            for (String part : parts) {
                String selector = part.trim();
                // Пропускаем пустые, медиа-запросы, псевдо-элементы
                if (!selector.isEmpty() &&
                        !selector.startsWith("@") &&
                        !selector.startsWith(":")) {
                    // Берём основной селектор (без псевдоклассов)
                    String mainSelector = selector.split(":")[0].trim();
                    if (!mainSelector.isEmpty()) {
                        selectorsSet.add(mainSelector);
                    }
                }
            }
        }

        return new ArrayList<>(selectorsSet);
    }

    private List<String> extractJsFunctions(String js) {
        List<String> functions = new ArrayList<>();
        // Ищем function name() или const name = function() или name() {}
        Pattern pattern = Pattern.compile("function\\s+(\\w+)\\s*\\(|(?:const|let|var)\\s+(\\w+)\\s*=\\s*function|(\\w+)\\s*\\([^)]*\\)\\s*\\{");
        Matcher matcher = pattern.matcher(js);

        while (matcher.find()) {
            String func = matcher.group(1);
            if (func == null) func = matcher.group(2);
            if (func == null) func = matcher.group(3);
            if (func != null && !func.isEmpty()) {
                functions.add(func);
            }
        }
        return functions;
    }

    private List<String> extractJsVariables(String js) {
        List<String> variables = new ArrayList<>();
        // Ищем объявления переменных
        Pattern pattern = Pattern.compile("(?:const|let|var)\\s+(\\w+)");
        Matcher matcher = pattern.matcher(js);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    private String getAllContent(List<Path> htmlFiles, List<Path> jsFiles) throws IOException {
        StringBuilder content = new StringBuilder();
        for (Path file : htmlFiles) {
            content.append(Files.readString(file));
        }
        for (Path file : jsFiles) {
            content.append(Files.readString(file));
        }
        return content.toString();
    }

    private int countOccurrences(String text, String word) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private long estimateUnusedSize(String content, List<String> unusedItems) {
        long size = 0;
        for (String item : unusedItems) {
            // Ищем размер фрагмента кода, содержащего этот селектор/функцию
            Pattern pattern = Pattern.compile("[^;{}]*" + Pattern.quote(item) + "[^;{}]*[;{}]");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                size += matcher.group().length();
            }
        }
        return size;
    }

    private String findSelectorInFile(String content, String selector) {
        Pattern pattern = Pattern.compile("[^;{}]*" + Pattern.quote(selector) + "[^;{}]*[;{}]");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String snippet = matcher.group().trim();
            if (snippet.length() > 200) {
                snippet = snippet.substring(0, 200) + "...";
            }
            return snippet;
        }
        return selector;
    }

    private String findFunctionInFile(String content, String functionName) {
        Pattern pattern = Pattern.compile("function\\s+" + Pattern.quote(functionName) + "\\s*\\([^)]*\\)\\s*\\{[^}]*\\}");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String snippet = matcher.group();
            if (snippet.length() > 300) {
                snippet = snippet.substring(0, 300) + "...";
            }
            return snippet;
        }
        return functionName;
    }

    private int findLineNumber(String content, String searchText) {
        String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(searchText)) {
                return i + 1; // номера строк с 1
            }
        }
        return 0;
    }

    private double calculateHealthScore(AnalysisSession session) {
        List<FileReport> reports = fileReportRepository.findByAnalysisId(session.getId());
        if (reports.isEmpty()) {
            return 100.0;
        }

        double totalUnusedPercentage = 0;
        for (FileReport report : reports) {
            totalUnusedPercentage += report.getUnusedPercentage();
        }
        double averageUnused = totalUnusedPercentage / reports.size();

        // HealthScore = 100 - средний процент мёртвого кода
        return Math.max(0, 100 - averageUnused);
    }

    // Вспомогательные методы для работы с файлами
    private void unzip(MultipartFile zipFile, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private List<Path> findFiles(Path dir, String extension) throws IOException {
        List<Path> result = new ArrayList<>();
        try (var stream = Files.walk(dir)) {
            stream.filter(path -> path.toString().endsWith(extension))
                    .forEach(result::add);
        }
        return result;
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            try (var stream = Files.walk(dir)) {
                stream.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                // ignore
                            }
                        });
            }
        }
    }
}