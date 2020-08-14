package jmstransaction.transactionJms.` transactionJMSConsumer `


class ConditionalService {
    @Throws(Exception::class)
    fun messageReceived(transaction: String): String {
        println("----------- messageReceived attempt " + count + "----------" + transaction + " " + Thread.currentThread().id)
        //transaction = transaction;
        count++
        // if(student != null){
        //throw new Exception();
        // }
        return transaction
    }

    @Throws(Exception::class)
    fun fileWriting1(transaction: Any): Any {
        println("----------- fileWriting1 attempt " + count + "----------" + transaction + " " + Thread.currentThread().id)
        count++
        //if(student != null){
        //throw new Exception();
        //}
        return transaction
    }

    @Throws(Exception::class)
    fun fileWriting2(transaction: Any): Any {
        println("----------- fileWriting2 ----------" + transaction + " " + Thread.currentThread().id)
        /*if(student.getGender() == Gender.FEMALE){
				throw new Exception();
			}*/return transaction
    }

    @Throws(Exception::class)
    fun jmsErrorFlow(transaction: Any): Any {
        println("----------- jmsErrorFlow attempt " + count + "----------" + transaction + " " + Thread.currentThread().id)
        count++
        //if(student != null){
        //throw new Exception();
        // }
        return transaction
    }

    companion object {
        var count = 0
    }
}