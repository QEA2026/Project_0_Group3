package com.group3;

import java.util.List;
import java.util.Scanner;

import com.group3.DAOs.ApprovalDAO;
import com.group3.DAOs.ExpenseDAO;
import com.group3.DAOs.UserDAO;
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
            System.out.println("Username:" );
            String username = scanner.nextLine().trim();
            System.out.println("Password: ");
            String password = scanner.nextLine();

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



        System.out.println("Welcome, " + manager.getUsername() + "!");
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
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            try{
                switch (choice){
                    case "1" -> viewPendingExpenses(service);
                    case "2" -> reviewExpense(scanner, manager, service);
                    case "3" -> commentOnExpense(scanner, manager, service);
                    case "4" -> {
                        System.out.println("Goodbye, " + manager.getUsername() + "!");
                        running = false;
                    }
                    default -> System.out.println("Please enter 1-4.");
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

        System.out.println("--- Pending Expenses ---");
        for (ExpenseWithApproval ea : pending){
            Expense e = ea.expense();
            System.out.printf("Expense #%d | $%.2f | %s | submitted %s | employee id %d%n",
                e.getId(), e.getAmount(), e.getDescription(),
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

        boolean success = service.approveOrDenyExpense(expenseId, manager.getId(), decision, comment);
        System.out.println(success
                ? "Expense " + expenseId + " " + decision.toLowerCase() + "."
                : "Update failed — please try again.");
    }

    private static void commentOnExpense(Scanner scanner, User manager, ExpenseApprovalsService service){
        int expenseId = promptForInt(scanner, "Expense id to comment on: ");
        System.out.println("New comment: ");
        String comment = scanner.nextLine();

        boolean success = service.addCommentToExpenseDecision(expenseId, manager.getId(), comment);

        System.out.println(success ? "Comment saved." : "Update failed - please try again.");
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
