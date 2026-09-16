# Privacy Policy for ExpenseLite

**Effective Date:** September 15, 2026

## 1. Overview
ExpenseLite is a fully local, offline-first personal expense tracking application designed with privacy as a foundational principle. ExpenseLite does not require account creation, sign-in, or internet connection to operate.

## 2. Information Collection and Use
- **Zero Telemetry & Analytics:** ExpenseLite does **NOT** collect, transmit, or share any personal information, usage analytics, device identifiers, or tracking telemetry.
- **Local Data Storage:** All financial transactions, categories, and application preferences are stored exclusively on your local device using an encrypted local SQLite/Room database and Preferences DataStore.
- **Data Sharing:** We do not sell, rent, share, or disclose any of your data to third parties.

## 3. Storage Access & File Backup
ExpenseLite utilizes Android's native Storage Access Framework (SAF) to enable user-initiated JSON backup export and import functionality.
- Exported backup files are stored directly in the local device storage location chosen by the user.
- ExpenseLite does not request persistent file system permissions (`READ_EXTERNAL_STORAGE` or `WRITE_EXTERNAL_STORAGE`).

## 4. Security
Your financial data never leaves your physical device unless you explicitly export a local backup file using SAF.

## 5. Contact Us
If you have questions regarding this Privacy Policy, please open an issue on the official ExpenseLite repository:
https://github.com/Amehran/ExpenseLite
