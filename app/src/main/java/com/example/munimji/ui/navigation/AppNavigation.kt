package com.example.munimji.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munimji.ui.screens.*
import com.example.munimji.ui.viewmodel.AppViewModel

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val repository = viewModel.repository

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            ProfessionalDashboardScreen(viewModel) { route ->
                navController.navigate(route)
            }
        }
        composable("sales_purchase") {
            SaleAndPurchaseScreen(viewModel)
        }
        composable("cheques") {
            ChequeManagerScreen(viewModel)
        }
        composable("inventory") {
            InventoryManagementScreen(viewModel)
        }
        composable("reports") {
            ReportsScreen(viewModel)
        }
        composable("auth") {
            AuthScreen(viewModel)
        }
        composable("purchase_report") {
            PurchaseReportScreen(viewModel) {
                navController.popBackStack()
            }
        }
        composable("sale_report") {
            SaleReportScreen(viewModel) {
                navController.popBackStack()
            }
        }
        composable("payment_receipt") {
            PaymentReceiptScreen {
                navController.popBackStack()
            }
        }
        composable("excel_import") {
            ExcelImportScreen(navController, repository)
        }
        composable("sales_analytics") {
            SalesAnalyticsScreen(navController, repository)
        }
    }
}

