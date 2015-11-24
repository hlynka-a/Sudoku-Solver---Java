package sudokuMain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class SudokuSolver {
    public final static boolean RIGHT_TO_LEFT = false;
    
    public static JTextField box[][] = new JTextField[9][9];
    public static JButton solve = new JButton("RUN");
    public static JButton reset = new JButton("RESET");
    public static JLabel answer = new JLabel("  MAYBE");
    static Font font1 = new Font("SansSerif", Font.BOLD, 20);
    
    public static ArrayList<String> choices = new ArrayList<String>();
    public static int nums[][] = new int[9][9];
    public static int completeNums[][] = new int[9][9];
    public static int setBounds = 1;
    public static int setLayer = 1;
    public static ArrayList<String>[][] mulChoices = new ArrayList[9][9];
    public static ArrayList<String> lastOneX = new ArrayList<String>();
    public static ArrayList<String> lastOneY = new ArrayList<String>();
    public static int numOfCycles = 0;
    public static int maxCycles = 729;
    
    public static void addComponentsToPane(Container contentPane) {
        if (RIGHT_TO_LEFT) {
            contentPane.setComponentOrientation(
                ComponentOrientation.RIGHT_TO_LEFT);
        }
//        Any number of rows and 2 columns
        contentPane.setLayout(new GridLayout(0,9));
        for (int i = 0; i < 9; i++)
        {
        	for (int j = 0; j < 9; j++)
        	{
        		box[i][j] = new JTextField(1);
        		if ((((i < 3)||(i>5))&&((j < 3)||(j>5)))
        			||(((i > 2)&&(i<6))&&((j > 2)&&(j < 6))))
        		{
        			box[i][j].setBackground(Color.gray);
        		}
        		else
        		{
        			box[i][j].setBackground(Color.white);
        		}
        		box[i][j].setHorizontalAlignment(0);
        		box[i][j].setFont(font1);
        		contentPane.add(box[i][j]);
        		
        		mulChoices[i][j] = new ArrayList<String>();
        	}
        }
        solve.addActionListener(new ActionListener()	//using "annonymous class", as
		{													//said in assignment, and described online
			public void actionPerformed(ActionEvent event)
			{
				if (checkIfSolvable())
				{
					answer.setText("solving...");
					
					while(startSolving()&&checkIfSolvable())
					{

					}
					
					for (int i = 0; i < 9; i++)
					{
						for (int j = 0; j < 9; j++)
						{
							if (nums[i][j]!=0)
							{
								box[i][j].setText(""+nums[i][j]);
							}
						}
					}
					
					if (answer.getText().compareTo("bad input")!=0)
						answer.setText("solved");
					
					choices.clear();
					for (int i = 0; i < 9; i++)
					{
						for (int j = 0; j < 9; j++)
						{
							setChoices();
							choices.removeAll(checkColumn(i,j));
							choices.remove(""+nums[i][j]);
							if (choices.size() > 0)
							{
								answer.setText("invalid");
							}
							setChoices();
							choices.removeAll(checkRows(i,j));
							choices.remove(""+nums[i][j]);
							if (choices.size() > 0)
							{
								answer.setText("invalid");
							}
							setChoices();
							choices.removeAll(checkBox(i,j));
							choices.remove(""+nums[i][j]);
							if (choices.size() > 0)
							{
								answer.setText("invalid");
							}
						}
					}
				}
				System.out.println("done");
			}
		}
		);
        contentPane.add(solve);
        contentPane.add(answer);
        reset.addActionListener(new ActionListener()	//using "annonymous class", as
		{													//said in assignment, and described online
			public void actionPerformed(ActionEvent event)
			{
					for (int i = 0; i < 9; i++)
					{
						for (int j = 0; j < 9; j++)
						{
							nums[i][j] = 0;
							box[i][j].setText("");
							setBounds = 1;
							setLayer = 1;
							numOfCycles = 0;
						}
					}
			}
		}
		);
        contentPane.add(reset);
    }
    
    public static boolean startSolving()
    {
    	boolean keepGoing = false;
    	if (!finished())
    	{
    		
			for (int i = 0; i < 9; i++)
			{
				for (int j = 0; j < 9; j++)
				{
					if (nums[i][j] == 0)
					{
						setChoices();
						choices.removeAll(checkColumn(i,j));
						choices.removeAll(checkRows(i,j));
						choices.removeAll(checkBox(i,j));
						if (choices.size() == setBounds)
						{
							nums[i][j] = Integer.parseInt(choices.get(0));
							choices.remove(0);
							keepGoing = true;
							completeNums[i][j] = setLayer;
							System.out.print(" Set " + i + ", " + j + " to " + nums[i][j] 
							          + ". setBounds = " + setBounds + ". setLayer = " + setLayer);
							if (setBounds > 1)
							{
								setBounds = 1;
								for (int q = 0; q < choices.size(); q++)
								{
									mulChoices[i][j].add(choices.get(q));
								}
								System.out.println("");
								System.out.print(i + " " + j + " could also be " + mulChoices[i][j].get(0)); 
								System.out.println(" of " + mulChoices[i][j].size());
								i = 0;
								j = 0;
							}
							fillTheBoxes();
						}
						else if ((choices.size() == 0)&&(setLayer>0))
						{
							System.out.println("");
							System.out.println(i + " " + j + " is impossible to fill.");
							boolean layerOver = true;
							while (layerOver)
							{
								for (int k = 0; k < 9; k++)
								{
									for (int l = 0; l < 9; l++)
									{
										if (completeNums[k][l] == setLayer)
										{
											completeNums[k][l] = 0;
											nums[k][l] = 0;
											if (mulChoices[k][l].size() > 0)
											{
												nums[k][l] = Integer.parseInt(mulChoices[k][l].get(0));
												System.out.println("Change " + k + ", " + l + " to " + nums[k][l]);
												mulChoices[k][l].remove(0);
												completeNums[k][l] = setLayer;
												layerOver = false;
											}
										}
									}	
								}
								i = 0;
								j = 0;
								System.out.println("Layer currently: "+setLayer);
								if (layerOver)
								{
									System.out.println("This level is going nowhere. Go back.");
									setLayer--;
								}
								if (setLayer < 0)
								{
									System.out.println("Error here.");
									answer.setText("impossible");
									return false;
									//System.out.close();
								}
							}
							fillTheBoxes();
						}
					}
				}
			}			
			for (int i = 0; i < 9; i++)
			{
				for (int j = 0; j < 9; j++)
				{
					if (nums[i][j]!=0)
					{
						box[i][j].setText(""+nums[i][j]);
					}
					else
					{
						box[i][j].setText("");
					}
				}
			}
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}*/
			////////
			if ((!finished())&&(keepGoing==false))
			{
				setLayer++;
				setBounds++;
				keepGoing = true;
			}

			
    	}
    	return keepGoing;
    }
    
    public static void fillTheBoxes()
    {
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				if (nums[i][j]!=0)
				{
					box[i][j].setText(""+nums[i][j]);
				}
				else
				{
					box[i][j].setText("");
				}
			}
		}
    }
    
    public static boolean finished()
    {
    	boolean finished = true;
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				if ((nums[i][j]!=1)&&(nums[i][j]!=2)&&(nums[i][j]!=3)
					&&(nums[i][j]!=4)&&(nums[i][j]!=5)&&(nums[i][j]!=6)
					&&(nums[i][j]!=7)&&(nums[i][j]!=8)&&(nums[i][j]!=9))
				{
					finished = false;
					break;
				}
			}
		}
    	return finished;
    }
    
    public static boolean checkIfSolvable()	//also check for logic errors.
    {
    	boolean solvable = true;
        for (int i = 0; i < 9; i++)
        {
        	for (int j = 0; j < 9; j++)
        	{
        		if ((box[i][j].getText().compareTo("1")==0)||(box[i][j].getText().compareTo("2")==0)
        				||(box[i][j].getText().compareTo("3")==0)||(box[i][j].getText().compareTo("4")==0)
        				||(box[i][j].getText().compareTo("5")==0)||(box[i][j].getText().compareTo("6")==0)
        				||(box[i][j].getText().compareTo("7")==0)||(box[i][j].getText().compareTo("8")==0)
        				||(box[i][j].getText().compareTo("9")==0))
        		{
        			nums[i][j] = Integer.parseInt(box[i][j].getText());
        		}
        		else if (box[i][j].getText().compareTo("")==0)
        		{
        			nums[i][j] = 0;
        			completeNums[i][j] = 0;
        		}
        		else
        		{
        			solvable = false;
        			break;
        		}
        	}
        }
        
        /*if (setLayer > 81)
        {
        	solvable = false;
        }*/
        numOfCycles++;
        if (numOfCycles > maxCycles)
        {
        	solvable = false;
        	System.out.println("Num of cycles exceeded max.");
        	answer.setText("bad input");
        }
        if (!solvable)
        {
        	answer.setText("bad input");
        }
        return solvable;
    }
    
    public static void setChoices()
    {
    	choices.removeAll(choices);
    	for (int i = 1; i < 10; i++)
    	{
    		choices.add("" + i);
    	}
    }
    
    public static ArrayList<String> checkBox(int i, int j)
    {
    	ArrayList<String> theseChoices = new ArrayList<String>();
    	if (((i==0)||(i==1)||(i==2))
    		&&((j==0)||(j==1)||(j==2)))		//first box
    	{
    		for (int k = 0; k < 3; k++)
    		{
    			for (int l = 0; l < 3; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}
    	}
    	else if (((i==0)||(i==1)||(i==2))
        	&&((j==3)||(j==4)||(j==5)))		//second box
    	{
    		for (int k = 0; k < 3; k++)
    		{
    			for (int l = 3; l < 6; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}
    	}
    	else if (((i==0)||(i==1)||(i==2))
        	&&((j==6)||(j==7)||(j==8)))		//third box
    	{
    		for (int k = 0; k < 3; k++)
    		{
    			for (int l = 6; l < 9; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}
    	}
    	else if (((i==3)||(i==4)||(i==5))
        	&&((j==0)||(j==1)||(j==2)))		//first box
        {
    		for (int k = 3; k < 6; k++)
    		{
    			for (int l = 0; l < 3; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}
        }
        else if (((i==3)||(i==4)||(i==5))
           	&&((j==3)||(j==4)||(j==5)))		//second box
        {
    		for (int k = 3; k < 6; k++)
    		{
    			for (int l = 3; l < 6; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}       	
        }
        else if (((i==3)||(i==4)||(i==5))
           	&&((j==6)||(j==7)||(j==8)))		//third box
        {
    		for (int k = 3; k < 6; k++)
    		{
    			for (int l = 6; l < 9; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}       		
        }
    	else if (((i==6)||(i==7)||(i==8))
            &&((j==0)||(j==1)||(j==2)))		//first box
        {
    		for (int k = 6; k < 9; k++)
    		{
    			for (int l = 0; l < 3; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}   		
        }
        else if (((i==6)||(i==7)||(i==8))
            &&((j==3)||(j==4)||(j==5)))		//second box
        {
    		for (int k = 6; k < 9; k++)
    		{
    			for (int l = 3; l < 6; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}             	
        }
        else if (((i==6)||(i==7)||(i==8))
            &&((j==6)||(j==7)||(j==8)))		//third box
        {
    		for (int k = 6; k < 9; k++)
    		{
    			for (int l = 6; l < 9; l++)
    			{
    				if (((k!=i)||(l!=j))&&(nums[k][l]!=0))
    				{
    					theseChoices.add("" + nums[k][l]);
    				}
    			}
    		}             		
        }   	
    	return theseChoices;
    }
    
    public static ArrayList<String> checkColumn(int i, int j)
    {
    	ArrayList<String> theseChoices = new ArrayList<String>();
    	for (int s = 0; s < 9; s++)
    	{   	//System.out.println("running...");
    		if (s!=i)
    		{
    			if (nums[s][j]!=0)
    			{
    				theseChoices.add(box[s][j].getText());
    			}
    		}
    	}
    	return theseChoices;
    }
    
    public static ArrayList<String> checkRows(int i, int j)
    {
    	ArrayList<String> theseChoices = new ArrayList<String>();
    	for (int s = 0; s < 9; s++)
    	{
    		if (s!=j)
    		{
    			if (nums[i][s]!=0)
    			{
    				theseChoices.add(box[i][s].getText());
    			}
    		}
    	}
    	return theseChoices;
    }
    
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("JAVA SUDOKU SOLVER 1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane and components in GridLayout
        addComponentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
