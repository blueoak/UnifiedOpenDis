package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public abstract class DisBitSet extends BitSet implements Marshaller
{

    private int bitLength;
    private int byteLength;

    public DisBitSet(int len)
    {
        super(len); // length from bitfield element
        bitLength = len;
        byteLength = (bitLength + Byte.SIZE - 1) / Byte.SIZE;
    }

    protected static int calculateMask(int position, int length)
    {
        int result = 0;
        for (int i = position; i < position + length; i++) {
            result |= (1 << i);
        }

        return result;
    }

    protected static int calculateMask(int length)
    {
        int ret = 0;
        for (int i = 0; i < length; i++) {
            ret |= 1 << i;
        }
        return ret;
    }

    protected void setbits(int pos, int len, int val)
    {try{
        for (int i = pos, j = 0; i < pos + len; i++, j++) {
            boolean isset = (val & (1 << j)) != 0;
            set(i, i + 1, isset); // BitSet class
        }
    }
    catch(Throwable t) {
        t.printStackTrace();
    }
    }

    public byte[] marshall()
    {
        byte[] ba = toByteArray();
        // BitField does not return an array equal in size to that passed to the constructor--it may be smaller.
        // This will put 0's at the end
        if (ba.length < byteLength)
            ba = Arrays.copyOf(ba, byteLength);
        return ba;
    }
    
    @Override
    public int getMarshalledSize()
    {
        return byteLength;
    }

    @Override
    public void marshal(DataOutputStream dos)
    {
        try {
          dos.write(marshall());
        }
        catch(IOException ex) {
            System.out.println(ex.getClass().getSimpleName()+": "+ex.getMessage());
        }
    }

    @Override
    public void unmarshal(DataInputStream dis)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void marshal(ByteBuffer buff)
    {
        buff.put(marshall());
    }

    @Override
    public void unmarshal(ByteBuffer buff)
    {
       /* byte[] ba = new byte[byteLength];       
        buff.get(ba);*/
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

}
