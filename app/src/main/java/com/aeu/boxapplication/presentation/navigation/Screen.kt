package com.aeu.boxapplication.presentation.navigation

sealed class Screen(val route: String) {
    // Onboarding
    object Welcome : Screen("welcome")
    object Loading : Screen("loading")

    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Admin
    object AdminDashboard : Screen("admin_dashboard")
    object UserManagement : Screen("user_management")
    object AdminSettings : Screen("admin_settings")
    
    // Subscriber
    object SubscriberHome : Screen("subscriber_home")
    object  SubscribDetail: Screen("subscribe_detail")
    object Profile : Screen("profile")
    object SubscriberSettings : Screen("subscriber_settings")

    object ProductDetails : Screen("product_details")
    object OrderHistory : Screen("order_history")
    object ShopProducts : Screen("shop_products")
    object ShoppingCart : Screen("shopping_cart")
    object ReOrder : Screen("reorder")
    object OrderConfirmed : Screen("order_confirmed")
    object OrderDetails : Screen("order_details")
    object  HistoryDetail : Screen("history_details")
    
    // Subscription
    object SubscriptionPlans : Screen("subscription_plans")
    object SubscriptionsEmpty : Screen("subscriptions_empty")
    object ExplorePlans : Screen("explore_plans")
    object ConfirmSubscription : Screen("confirm_subscription")
    object PlanDetails : Screen("plan_details/{planId}") {
        fun createRoute(planId: String) = "plan_details/$planId"
    }
    object MySubscription : Screen("my_subscription")
    object PauseSubscription : Screen("pause_subscription")
    object CancelSubscription : Screen("cancel_subscription")
    object UpgradeSubscription : Screen("upgrade_subscription")
    
    // Payment
    object CheckoutPayment : Screen("checkout_payment")
    object  CompletePayment : Screen("complete_payment")
    object OrderPlaced : Screen("order_placed")
    object PaymentHistory : Screen("payment_history")
    object PaymentMethods : Screen("payment_methods")
    object AddPaymentMethod : Screen("add_payment_method")
    object PaymentDetails : Screen("payment_details")
    object PaymentConfirmation : Screen("payment_confirmation")
    
    // Inventory
    object InventoryList : Screen("inventory_list")
    object InventoryAllocation : Screen("inventory_allocation")
    object InventoryTracking : Screen("inventory_tracking")
    
    // Shipment
    object ShipmentManifest : Screen("shipment_manifest")
    object ShipmentTracking : Screen("shipment_tracking")
    object ShipmentHistory : Screen("shipment_history")
    
    // Customer
    object CustomerProfile : Screen("customer_profile")
    object EditProfile : Screen("edit_profile")
    object Preferences : Screen("preferences")
    
    // Coupon
    object CouponList : Screen("coupon_list")
    object ApplyCoupon : Screen("apply_coupon")
    object CreateCoupon : Screen("create_coupon")
    
    // Notifications
    object NotificationInbox : Screen("notification_inbox")
    object NotificationSettings : Screen("notification_settings")
    
    // Reports
    object ReportsDashboard : Screen("reports_dashboard")
    object ChurnRate : Screen("churn_rate")
    object RevenueReport : Screen("revenue_report")
    object ActiveSubscribers : Screen("active_subscribers")
}
