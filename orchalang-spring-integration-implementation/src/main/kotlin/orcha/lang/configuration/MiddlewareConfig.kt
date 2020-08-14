package orcha.lang.configuration

data class MiddlewareConfig ( val  requestDestinationName : String="blockChainInputQueue",
                              val  requiresReply : Boolean = true ,
                              val replyDestinationName: String? = "enrollStudentQueue")