package dev.furtor.contastudenti;

public class Topic {
    private final String topicName;
    private int maxStudenti;

    //costruttore di default senza numero max di studenti
    public Topic(String topicName) {
        this.topicName = topicName;
        this.maxStudenti = 100;
    }

    public Topic(String topicName, int maxStudenti) {
        this.topicName = topicName;
        this.maxStudenti = maxStudenti;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getMaxStudenti() {
        return maxStudenti;
    }
}
