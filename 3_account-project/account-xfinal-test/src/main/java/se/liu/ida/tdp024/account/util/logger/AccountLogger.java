package se.liu.ida.tdp024.account.util.logger;

public interface AccountLogger {

    enum TodoLoggerLevel {
        DEBUG,
        INFO,
        NOTIFY,
        WARNING,
        ERROR,
        CRITICAL,
        ALERT,
        EMERGENCY
    }
    
    public void log(Throwable throwable);
    
    public void log(TodoLoggerLevel todoLoggerLevel, String shortMessage, String longMessage);

    public void sendKafka(String kafkaTopic, TodoLoggerLevel todoLoggerLevel, String context, String info);
    
}
