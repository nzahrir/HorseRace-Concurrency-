import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.awt.Color.*;


public class HorseRace {
    JFrame frame;

    JProgressBar h1 = new JProgressBar(0,100),
                 h2 = new JProgressBar(0,100),
                 h3 = new JProgressBar(0,100),
                 h4 = new JProgressBar(0,100),
                 h5 = new JProgressBar(0,100);

    List<JProgressBar> horses = List.of(h1,h2,h3,h4,h5);
    List<Color> colors = List.of(RED, BLUE, GREEN, CYAN, YELLOW);
    int numberOfHorses = horses.size();

    JLabel msg = new JLabel("");
    static boolean isRaceButtonPressed = false;
    static boolean isResetButtonPressed = false;

    static int winningHorse = 0;
    static boolean winner = false;

    CyclicBarrier getSet = new CyclicBarrier(numberOfHorses);


    public HorseRace(){
        setUpFrame();
        addHorses(horses);
        addButtons();
    }
    private void setUpFrame(){
        frame = new JFrame();
        frame.setBounds(100,100,598,430);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
    }

    private void addHorses(List<JProgressBar> horses){

        for(int i = 0; i < horses.size(); i++){
            JProgressBar horse = horses.get(i);

            horse.setStringPainted(true);
            horse.setForeground(colors.get(i));
            horse.setBackground(colors.get(i));
            horse.setBounds(150,129 + (i*30),259,14);
            System.out.println(horse.toString());
            frame.getContentPane().add(horse);

        }
    }

    private void go(JProgressBar currentHorse,int stable){
        System.out.println("Horse #: " + stable);
        for(int i = 0; i <= 100; ++i) {
                if (winner) {
                    break;
                }
                currentHorse.setValue(i);
                currentHorse.repaint();
                if (i == 100) {
                    winningHorse = stable+1;
                    finish();
                }
                try{
                    Thread.sleep(Math.abs(UUID.randomUUID().getMostSignificantBits()%60));
                } catch(InterruptedException err){
                    err.printStackTrace();
                }
            };
    }


    private void onYourMarks(CyclicBarrier getSet, JProgressBar h, int i){
        try{
            System.out.println("Lining Up");
            getSet.await();
            go(h,i);
        } catch(InterruptedException | BrokenBarrierException e){

        }
   }

    public void addButtons(){

        ActionListener runRace = (ActionEvent e) -> {
            horses.stream().forEach(h -> h.setValue(5));
            if(!isRaceButtonPressed){
                msg.setVisible(false);
                isResetButtonPressed = false;
                isRaceButtonPressed = true;
                ExecutorService service = Executors.newFixedThreadPool(20);
                try{
                    horses.stream().forEach(h -> service.submit(()-> this.onYourMarks(getSet,h,horses.indexOf(h))));
                } finally {
                    service.shutdown();
                }
            }
            };

        msg.setBounds(85,100,410,14);
        msg.setVisible(false);
        frame.getContentPane().add(msg);

        JButton btnStart = new JButton("Run Race");
        btnStart.setFont(new Font("Tahoma", Font.PLAIN, 18));
        btnStart.addActionListener(runRace);
        btnStart.setBounds(50,287,155,40);
        frame.getContentPane().add(btnStart);

        JButton btnQuit = new JButton("Quit");
        btnQuit.setFont(new Font("Tahoma", Font.PLAIN, 18));
        //btnQuit.addActionListener(new QuitRacing());
        btnQuit.setBounds(50,287,155,40);
        frame.getContentPane().add(btnQuit);
    }


    public synchronized void finish(){
        msg.setVisible(true);
        msg.setText("Horse #" + winningHorse + " won the race!");

        winner = true;
        System.out.println("Horse #" + winningHorse + " won the race!");

        frame.getContentPane().add(msg);
        isRaceButtonPressed = false;

    }

    public static void main(String[] args) {
            HorseRace window = new HorseRace();
            window.frame.setVisible(true);
    }

}
