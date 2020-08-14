package jmstransaction.transactionJms;

public class ConditionalService {

    static int count = 0;

    public Object beforeSendingJMS(Object transaction) throws Exception {
        System.out.println("----------- beforeSendingJMS ----------" + transaction + " " + Thread.currentThread().getId());
        //	if(student != null){
        //		throw new Exception();
        //	}
        return transaction;
    }

    public Object jmsErrorFlow(Object transaction) throws Exception {
        System.out.println("----------- jmsErrorFlow attempt " + count + "----------" + transaction + " " + Thread.currentThread().getId());
        count++;
        // if(student != null){
        //     throw new Exception();
        // }
        return transaction;
    }

    public Object jmsReplyChannelFlow(Object transaction) throws Exception {
        System.out.println("----------- jmsReplyChannelFlow attempt " + count + "----------" + transaction + " " + Thread.currentThread().getId());
        count++;
        // if(student != null){
        //     throw new Exception();
        //}
        return transaction;
    }

}