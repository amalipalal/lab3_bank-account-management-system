# Banking System Terminal Application

## **Overview**

A Java-based terminal bank account management application that simulates core banking operations using **object-oriented design** and **layered architecture**. The system supports account management, transaction processing, and reporting, following SOLID principles.

***

## **Design**

### **Architecture**

*   **UI Layer**: Handles user input and menu navigation.
*   **Service Layer (Managers & Services)**: Orchestrates workflows between managers and domains.
*   **Domain Layer**: Represents data stores, entities, and business objects.
*   **Utility Layer**: Provides helper functions, validations, and common reusable logic.

## **Features**

*   Create accounts with customer and account type.
*   Deposit and withdraw funds with confirmation.
*   View all accounts and transaction history in formatted tables.
*   Multistep flows for account creation and transaction processing.
*   Persistent storage of application data (Transactions and Account creation).
*   Concurrent Transactions (multiple transaction execution at similar time)

***

## **How to Run**

1.  **Clone the repository**:
    ```bash
    git clone <repo-url>
    cd bank-account-management-system
    ```
2.  **Make the scripts executable**:
    ```bash
    chmod +x ./scripts/run.sh
    ```
3.  **Run the application**:
    ```bash
    ./scripts/run.sh
    ```
4.  **Navigate using menu options**:
    *   `1` → Create Account
    *   `2` → View Accounts
    *   `3` → Process Transaction
    *   `4` → View Transaction History
    *   `5` → Run tests
    *   `6` → Exit

***
