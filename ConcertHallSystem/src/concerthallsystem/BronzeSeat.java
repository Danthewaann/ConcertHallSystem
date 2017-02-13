package concerthallsystem;

public class BronzeSeat extends Seat implements Comparable 
{   
    public BronzeSeat(String row, int num, int index)
    {
        super(row, num, index);
    }

    @Override
    public int compareTo(Object t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

