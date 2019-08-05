package service.transport


import java.util.ArrayList
import java.util.Date

class Request {

    var identifier: Int = 0
    val products = ArrayList<ProductDesignation>()
    val deliveryTime: DeliveryTime? = null
    var date: Date? = null
    val placeOfDelivery: PlaceOfDelivery? = null
    var customer: Customer? = null
    val bill: Bill? = null

    enum class DeliveryTime private constructor(val numberOfDays: Int) {

        URGENT(1),
        FAST(3),
        NORMAL(5)

    }

    enum class PlaceOfDelivery {
        AT_HOME,
        DELIVERY_CENTER
    }

}
