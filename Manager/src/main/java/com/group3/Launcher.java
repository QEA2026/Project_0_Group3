package com.group3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.group3.DAOs.ApprovalDAO;
import com.group3.DAOs.ExpenseDAO;
import com.group3.DAOs.UserDAO;
import com.group3.models.Approval;
import com.group3.models.CategoryTotal;
import com.group3.models.EmployeeSummary;
import com.group3.models.Expense;
import com.group3.models.ExpenseWithApproval;
import com.group3.models.User;
import com.group3.service.ExpenseApprovalsService;
import com.group3.service.UserService;


public class Launcher {
    public static void main(String[] args) {
    
        UserService userService = new UserService(new UserDAO());
        ExpenseApprovalsService approvalService = new ExpenseApprovalsService(new ApprovalDAO(), new ExpenseDAO());
        Scanner scanner = new Scanner(System.in);
        User manager = null;

        System.out.println("=== Manager Portal Login ===");

        for(int attempts = 0; attempts < 3 && manager == null; attempts++){
            System.out.print("Username: " );
            String username = scanner.nextLine().trim();
            String password = readPassword(scanner, "Password: ");

            try{
                manager = userService.login(username, password);
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
            }
        }
        if (manager == null){
            System.out.println("Too many failed attempts. Exiting.");
            scanner.close();
            return;
        }



        System.out.println("\nWelcome, " + manager.getUsername() + "!");
        runManagerMenu(scanner, manager, approvalService);    
        scanner.close();
        
    }

    private static void runManagerMenu(Scanner scanner, User manager, ExpenseApprovalsService service){
        boolean running = true;
        while (running){
            System.out.println("\n=== Manager Menu ===");
            System.out.println("1. View pending expenses");
            System.out.println("2. Approve or deny an expense");
            System.out.println("3. Add comment to a reviewed expense");
            System.out.println("4. Report: spending by category");
            System.out.println("5. Report: spending by employee");
            System.out.println("6. Report: expenses by date range");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            try{
                switch (choice){
                    case "1" -> viewPendingExpenses(service);
                    case "2" -> reviewExpense(scanner, manager, service);
                    case "3" -> commentOnExpense(scanner, manager, service);
                    case "4" -> showCategoryReport(service);
                    case "5" -> showEmployeeReport(service);
                    case "6" -> showDateReport(scanner, service);
                    case "7" -> {
                        System.out.println("Goodbye, " + manager.getUsername() + "!");
                        running = false;
                    }
                    default -> System.out.println("Please enter 1-7.");
                }
            }
            catch (IllegalArgumentException e){
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

    private static void viewPendingExpenses(ExpenseApprovalsService service) {
        List<ExpenseWithApproval> pending = service.getPendingExpenses();
        
        if(pending.isEmpty()){
            System.out.println("No pending expenses. All caught up!");
            return;
        }

        System.out.println("\n" + centered("--- Pending Expenses ---", 80));
        System.out.printf("%-4s | %9s | %-25s | %-15s | %-10s | %s%n",
            "ID", "Amount", "Description", "Category", "Date", "Emp");
        System.out.println("-".repeat(80));
        for (ExpenseWithApproval ea : pending){
            Expense e = ea.expense();
            System.out.printf("%-4d | $%8.2f | %-25s | %-15s | %-10s | %3d%n",
                e.getId(), e.getAmount(), fit(e.getDescription(), 25), fit(e.getCategory(), 15),
                e.getExpense_date(), e.getUser_id_fk());
        }
    }

    private static void reviewExpense(Scanner scanner, User manager, ExpenseApprovalsService service) {
        viewPendingExpenses(service);
        
        int expenseId = promptForInt(scanner, "Expense id to review: ");
        
        System.out.print("Decision (approved/denied): ");
        String decision = scanner.nextLine().trim();
        
        System.out.print("Comment for the employee: ");
        String comment = scanner.nextLine();

        System.out.println("Categories: " + String.join(", ", ExpenseApprovalsService.CATEGORIES));
        System.out.print("Category (press Enter to keep current): ");
        String category = scanner.nextLine();

        boolean success = service.approveOrDenyExpense(expenseId, manager.getId(), decision, comment, category);
        System.out.println(success
                ? "Expense " + expenseId + " " + decision.toLowerCase() + "."
                : "Update failed — please try again.");
    }

    private static void commentOnExpense(Scanner scanner, User manager, ExpenseApprovalsService service){
        List<ExpenseWithApproval> reviewed = service.getReviewedExpenses();

        if (reviewed.isEmpty()){
            System.out.println("No reviewed expenses yet — approve or deny one first.");
            return;
        }

        System.out.println("\n" + centered("--- Reviewed Expenses ---", 83));
        System.out.printf("%-4s | %9s | %-20s | %-8s | %-30s%n",
            "ID", "Amount", "Description", "Status", "Comment");
        System.out.println("-".repeat(83));
        for (ExpenseWithApproval ea : reviewed){
            Expense e = ea.expense();
            Approval a = ea.approval();
            System.out.printf("%-4d | $%8.2f | %-20s | %-8s | %-30s%n",
                e.getId(), e.getAmount(), fit(e.getDescription(), 20),
                a.getStatus(), fit(a.getComment(), 30));
        }

        int expenseId = promptForInt(scanner, "Expense id to comment on: ");
        System.out.print("New comment: ");
        String comment = scanner.nextLine();

        boolean success = service.addCommentToExpenseDecision(expenseId, manager.getId(), comment);

        System.out.println(success ? "Comment saved." : "Update failed - please try again.");
    }

    private static void showCategoryReport(ExpenseApprovalsService service){
        List<CategoryTotal> report = service.getSpendingByCategory();

        if (report.isEmpty()){
            System.out.println("No expenses to report on yet.");
            return;
        }

        StringBuilder out = new StringBuilder();
        out.append(centered("--- Spending by Category ---", 40)).append(System.lineSeparator());
        out.append(String.format("%-15s | %8s | %10s%n", "Category", "Expenses", "Total"));
        out.append("-".repeat(40)).append(System.lineSeparator());
        for (CategoryTotal row : report){
            out.append(String.format("%-15s | %8d | $%9.2f%n",
                row.category(), row.expenseCount(), row.totalAmount()));
        }
        System.out.println();
        System.out.print(out);
        saveReport("spending-by-category", out.toString());
    }

    private static void showEmployeeReport(ExpenseApprovalsService service){
        List<EmployeeSummary> report = service.getSpendingByEmployee();

        if (report.isEmpty()){
            System.out.println("No expenses to report on yet.");
            return;
        }

        StringBuilder out = new StringBuilder();
        out.append(centered("--- Spending by Employee ---", 70)).append(System.lineSeparator());
        out.append(String.format("%-15s | %8s | %10s | %8s | %6s | %7s%n",
            "Employee", "Expenses", "Total", "Approved", "Denied", "Pending"));
        out.append("-".repeat(70)).append(System.lineSeparator());
        for (EmployeeSummary row : report){
            out.append(String.format("%-15s | %8d | $%9.2f | %8d | %6d | %7d%n",
                fit(row.username(), 15), row.totalExpenses(), row.totalAmount(),
                row.approved(), row.denied(), row.pending()));
        }
        System.out.println();
        System.out.print(out);
        saveReport("spending-by-employee", out.toString());
    }

    private static void showDateReport(Scanner scanner, ExpenseApprovalsService service){
        System.out.print("Start date (YYYY-MM-DD): ");
        String start = scanner.nextLine().trim();
        System.out.print("End date (YYYY-MM-DD): ");
        String end = scanner.nextLine().trim();

        List<ExpenseWithApproval> rows = service.getExpensesBetweenDates(start, end);

        if (rows.isEmpty()){
            System.out.println("No expenses between " + start + " and " + end + ".");
            return;
        }

        StringBuilder out = new StringBuilder();
        out.append(centered("--- Expenses " + start + " to " + end + " ---", 84)).append(System.lineSeparator());
        out.append(String.format("%-4s | %-10s | %9s | %-25s | %-15s | %-8s%n",
            "ID", "Date", "Amount", "Description", "Category", "Status"));
        out.append("-".repeat(84)).append(System.lineSeparator());
        double total = 0;
        for (ExpenseWithApproval ea : rows){
            Expense e = ea.expense();
            out.append(String.format("%-4d | %-10s | $%8.2f | %-25s | %-15s | %-8s%n",
                e.getId(), e.getExpense_date(), e.getAmount(),
                fit(e.getDescription(), 25), fit(e.getCategory(), 15),
                ea.approval().getStatus()));
            total += e.getAmount();
        }
        out.append("-".repeat(84)).append(System.lineSeparator());
        out.append(String.format("Total: $%.2f across %d expense(s)%n", total, rows.size()));
        System.out.println();
        System.out.print(out);
        saveReport("expenses-" + start + "-to-" + end, out.toString());
    }

    // writes a report to reports/<name>_<timestamp>.txt so it can be reviewed later
    private static void saveReport(String name, String content) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
        Path file = Path.of("reports", name + "_" + timestamp + ".txt");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content);
            System.out.println("Report saved to " + file);
        } catch (IOException e) {
            System.out.println("Could not save report to file: " + e.getMessage());
        }
    }

    // centers a title over a table of the given width
    private static String centered(String title, int width) {
        int padding = Math.max(0, (width - title.length()) / 2);
        return " ".repeat(padding) + title;
    }

    // pads or truncates a value so every row uses exactly the same column width
    private static String fit(String value, int width) {
        if (value == null) return "";
        return value.length() <= width ? value : value.substring(0, width - 3) + "...";
    }

    

    // echoes * for each typed character, like a typical login form;
    // falls back to plain visible input when no real terminal is attached
    private static String readPassword(Scanner scanner, String prompt) {
        try (org.jline.terminal.Terminal terminal =
                org.jline.terminal.TerminalBuilder.builder().system(true).dumb(true).build()) {
            if (!terminal.getType().startsWith("dumb")) {
                return org.jline.reader.LineReaderBuilder.builder()
                        .terminal(terminal).build()
                        .readLine(prompt, '*');
            }
        } catch (Exception ignored) {
            // no usable terminal - use the visible fallback below
        }
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int promptForInt(Scanner scanner, String prompt) {
        while (true) {                                   
            
            System.out.print(prompt);                    
            String input = scanner.nextLine().trim();    
            
            try {
                return Integer.parseInt(input);          
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");  
            }
        }
    }
   
}
