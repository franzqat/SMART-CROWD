package dev.furtor.contastudenti;

public class Topic {

    private final String topicName;
    private int maxStudenti;
    private  int accessPoints;



    //costruttore di default senza numero max di studenti
    public Topic(String topicName) {
        this.topicName = topicName;
        this.maxStudenti = 100;
        this.accessPoints = 1;
    }
    //costruttore di default senza numero di accesspoints
    public Topic(String topicName, int maxStudenti) {
        this.topicName = topicName;
        this.maxStudenti = maxStudenti;
        this.accessPoints = 1;
    }

    public Topic(String topicName, int maxStudenti, int accessPoints) {
        this.topicName = topicName;
        this.maxStudenti = maxStudenti;
        this.accessPoints = accessPoints;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getMaxStudenti() {
        return maxStudenti;
    }

    public void setMaxStudenti(int maxStudenti) {
        this.maxStudenti = maxStudenti;
    }

    public int getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(int accessPoints) {
        this.accessPoints = accessPoints;
    }
}
