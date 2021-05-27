package Components;

import Processes.ProcessInterface;
import Resources.Resource;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Word;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;

public class Memory extends Resource
{
    private int blockNumber = 0;
    private int wordNumber =  0;
    private  Word[][] memory = null;
    private final Deque<Integer> space;
    private final int step;

    public Memory(ProcessInterface father, ResourceEnum.Name name, int blockNumber, int step)
    {
        super(father, name, ResourceEnum.Type.STATIC);
        this.step = step;
        this.blockNumber = blockNumber;
        this.wordNumber = blockNumber*Constants.BLOCK_LENGTH;
        memory = new Word[blockNumber][Constants.BLOCK_LENGTH];
        space = new ArrayDeque<Integer>(100);

        for (int i = 0; i<blockNumber; i+=step){
            space.push(i);
        }

        for (int i = 0;i<blockNumber; i++)
        {
            for (int j = 0;j<Constants.BLOCK_LENGTH; j++)
            {
                try {
                    memory[i][j] = new Word(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getFreeSpaceBeginAddress(){
        return space.pop();
    }

    public void cleanSpace(int begin) throws Exception {
        if(begin%step!=0)throw new Exception("Wrong begin block");
        for (int i = begin; i<begin+step; i++){
            cleanBlock(i);
        }
        space.push(begin);
    }

    public boolean hasFreeSpace()
    {
        return space.size() != 0;
    }

    public boolean checkIfBlockEmpty(int block) throws Exception {
        if (block>=blockNumber) throw new Exception("Not existing block");
        for (int i=0;i<Constants.BLOCK_LENGTH;i++){
            if(memory[block][i].getNumber()!=0)return false;
        }
        return true;
    }

    public void cleanBlock(int block) throws Exception {
        if (block>=blockNumber) throw new Exception("Not existing block");
        for (int i=0;i<Constants.BLOCK_LENGTH;i++){
            memory[block][i] = new Word(0);
        }
    }

    public Word[] getBlock(int block)throws Exception {
        if (block>=blockNumber) throw new Exception("Not existing block");
        return memory[block];
    }
    public  void setBlock(int block, Word[] data)throws Exception {
        if (block>=blockNumber) throw new Exception("Not existing block");
        if (data.length!=Constants.BLOCK_LENGTH) throw new Exception("BAD block length");
        memory[block]= data.clone();
    }

    public int getMax(){
        return this.wordNumber;
    }
    public int getBlockBeginAddress(int block){
        return block*Constants.BLOCK_LENGTH;
    }
    public int [] getBlockAndWord(long virtualAddress) throws Exception
    {
        if(virtualAddress<0 || virtualAddress>=wordNumber)
        {
            System.out.println(" - - - -" + virtualAddress);
            throw new Exception("Not existing address");
        }
        int block = (int) (virtualAddress/Constants.BLOCK_LENGTH);
        int word = (int) (virtualAddress % Constants.BLOCK_LENGTH);
        return  new int[]{block,word};
    }

    public Word getWord(long virtualAddress) throws Exception
    {
        int address[] = getBlockAndWord(virtualAddress);
        int block = address[0];
        int word = address[1];
        return memory[block][word];
    }

    public Word getWord(String virtualAddress) throws Exception
    {
        int decimalAddress = Integer.parseInt(virtualAddress, 16);
        return getWord(decimalAddress);
    }

    public Word getWord(Word virtualAddress) throws Exception
    {
        return getWord(virtualAddress.getNumber());
    }


    public void setWord(Word word, long virtualAddress) throws Exception {
        getWord(virtualAddress).setWord(word);
    }

    public void setWord(Word word, Word virtualAddress) throws Exception {
        getWord(virtualAddress.getNumber()).setWord(word);
    }

    private String createWord(int val)
    {
        String hex = Integer.toHexString(val);
        for (int i = hex.length(); i < Constants.WORD_LENGTH; i++)
        {
            hex = "0"+hex;
        }
        return hex;
    }

    public void print()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("the-file-name.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0;i<Constants.BLOCK_NUMBER; i++)
        {
            for (int j = 0;j<Constants.BLOCK_LENGTH; j++)
            {
                writer.print(memory[i][j].toString());
                writer.print("   ");
            }
            writer.println("");
        }
        writer.close();
    }

}
