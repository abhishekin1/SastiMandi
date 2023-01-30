package abhishekg.pkart.model

data class AllOrderModel(
//    val name : String? = "",
//    val orderId : String?="",
//
//    val price : String?="",
//
//    val productId : String?="",
//
//    var status : String?="",
//
//    val userId : String?="",
//
//    val userName : String?="",
    val dateTime : String?="",
    val orderId : String?="",
    val pNameList : ArrayList<String> = ArrayList(),
    val pPriceList : ArrayList<String> = ArrayList(),
    val pQuanList : ArrayList<String> = ArrayList(),
    val status : String?="",
    val totalMoney : String?="",
    val userId : String?="",
    val userName : String?="",
    val address : String?="",
    val paymentMode : String?="",
    var expand : Boolean = false




)
