package transactionJMSConsumer;

public class ConditionalService {

    static int count = 0;

    public String messageReceived(String transaction) throws Exception {
        System.out.println("----------- messageReceived attempt " + count + "----------" + transaction + " " + Thread.currentThread().getId());
        //transaction = transaction;
        count++;
       // if(student != null){
            //throw new Exception();
       // }
        return transaction;
    }

    public Object fileWriting1(Object transaction) throws Exception {
        System.out.println("----------- fileWriting1 attempt " + count + "----------" + transaction+ " " + Thread.currentThread().getId());
        count++;
        //if(student != null){
            //throw new Exception();
        //}
        return transaction;
    }

    public Object fileWriting2(Object transaction) throws Exception {
        System.out.println("----------- fileWriting2 ----------" + transaction + " " + Thread.currentThread().getId());
			/*if(student.getGender() == Gender.FEMALE){
				throw new Exception();
			}*/
        return transaction;
    }

    public Object jmsErrorFlow(Object transaction) throws Exception {
        System.out.println("----------- jmsErrorFlow attempt " + count + "----------" + transaction + " " + Thread.currentThread().getId());
        count++;
        //if(student != null){
            //throw new Exception();
       // }
        return transaction;
    }

}