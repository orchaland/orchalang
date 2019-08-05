package service.transport

import java.util.Calendar

class ProfileUser {

    internal fun check(request: Request): Request {
        request.date = Calendar.getInstance().time
        request.identifier = 123
        request.customer = Customer()
        return request
    }
}
