import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.*;
import javax.imageio.ImageIO;

public class BoardImpl extends JFrame{																//	Clasa Primara, extinde JFrame	

	private static final long serialVersionUID = 1L;
	
	private BoardPanel panel;																		// Cream plansa de joc
	private int round = 0;																			// cream aleatoriu runda, care poate fi doar 0 sau 1, comutand la fiecare miscare intre 0 si 1, reprezentand randul fiecarui jucator
	private boolean finished;
	private BufferedReader textReader;
	private ArrayList<String> lines;
	private boolean withComputer;
	
	private final int[][] BOARDMAP = new int[][]{{ 2 , 3 , 7 , 12 , 18 , 19 , 26 , 27 , 33 },				// O matrice nemodificabila ce contine locurile de pe plansa in care exista "scurtaturi"
										         { 8 , 5 , 4 , 24 , 20 , 15 , 28 , 31 , 22 }};
	 
	public static void main(String args[]) throws Exception
	{
	
		String defaultname = " ";
		Color defaultcolor = Color.white;
		BoardImpl board = new BoardImpl();	

		board.textReader = new BufferedReader(new InputStreamReader(board.getClass().getResourceAsStream("/intrebari.txt")));
		board.lines = new ArrayList<String>();
		
		Player player1 = new Player(20,545, defaultname, defaultcolor);								// Cream cei 2 jucatori
		Player player2 = new Player(20,505, defaultname, defaultcolor);
		
		UserInputFrame uiframe = new UserInputFrame(player1, player2, board);
		uiframe.startUIFrame(player1, player2);
		
	}

	void start(BoardImpl board, Player player1, Player player2, boolean withComputer) throws Exception{
	
	  this.withComputer=withComputer;
	  Image snakesboard = new ImageIcon("Snakes_board.png").getImage();							// Incarcam imaginile necesare
	    
	  try {
		snakesboard = ImageIO.read(board.getClass().getResource("Snakes_board.png"));			// Pentru ca imaginile sa apara intr-un runnable jar, trebuie sa fie "invocate" ca si resurse.
		} catch (IOException e) { e.printStackTrace(); }
		
	  board.setLayout(null);
	  panel = new BoardPanel(snakesboard, player1, player2);
	  panel.setLocation(0,0);
	    
	  JTextPane outputpanel = new JTextPane();
	  outputpanel.setContentType("text/html");
	  outputpanel.setEditable(false);
	  JScrollPane scrollpanel = new JScrollPane(outputpanel);

	  scrollpanel.setSize(274, 470);
	  scrollpanel.setLocation(618, 30);
	  
	  if(getRound()==1) writeOnTextPanel(outputpanel, player2);
	  else writeOnTextPanel(outputpanel, player1);

	  UserInterface ui = new UserInterface(this, panel, player1, player2, round, outputpanel);
	  ui.setLocation(610,520);
	  
      add(ui);																						// Adaugam Interfata de control in partea dreapta.
      add(panel);																					// Adaugam plansa in partea stanga.
      add(scrollpanel);
      
      setVisible(true);																				// Modificam parametrii ferestrei, la o dimensiune optima, nemodificabila.
      setTitle("Game");
      setSize(910,625);
      setResizable(false);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public String[]  popQuestion(BoardImpl board) throws IOException{								// Citim fisierul text cu intrebari si returnam cinci valori( o intrebare si 4 raspunsuri)
		
		String[] x;
		String line;																				// Folosim o lista de stringuri pentru a stoca toate liniile din fisierul text
		
		if(lines.isEmpty()){																		// Daca lista e goala, o reumplem cu liniile din fisierul text.
			board.textReader = new BufferedReader(new InputStreamReader(board.getClass().getResourceAsStream("/intrebari2.txt")));
			while ((line = textReader.readLine()) != null) {
				lines.add(line);
			}
		}
		int aux = (int)(Math.random()*(lines.size()));												// Alegem aleator un numar, pe care il prelucram ca sa se termine in 1 sau in 6, avand in vedere ca pe liniile x1 si x6 se afla o intrebare, iar pe urmatoarele 4 linii se afla raspunurile la intrebare
		
		if(aux%10<6) aux=(aux/10)*10+0;
		else aux=(aux/10)*10+5;
		
		x= new String[]{lines.get(aux),lines.get(aux+1),lines.get(aux+2),lines.get(aux+3),lines.get(aux+4)};	
																									// Returnam intrebarea si raspunsruile
		for(int i=0; i<5;i++)lines.remove(aux);														// Stergem intrebarea si raspunsurile din lista, ca sa fim siguri ca nu mai apare o vreme.
		
	
		return x;
	}
	
	public int requiredNoSquares(int squareNo){														// Verificam plansa pentru "scurtaturi" si returnam numarul de patratele pe care trebuie sa-l sarim daca exista o scurtatura in patratelul in care ne aflam, sau -100 daca nu e un patratel special.
		int x = -100;
		
		for(int i = 0; i < 9; i++)
			if( squareNo == BOARDMAP[0][i])
				x = BOARDMAP[1][i] - BOARDMAP[0][i];
		
		return x;
	}
	
	public void writeOnTextPanel(JTextPane outputpanel, Player player) throws BadLocationException{
			outputpanel.setText(""); 																// Cream metoda prin care modificam textul din panoul text in interiorul jocului
			StyledDocument doc = outputpanel.getStyledDocument();
			
			SimpleAttributeSet keyWord = new SimpleAttributeSet();									// Definim formatarile( marimea scrisului, ingrosarea, etc)
			
			StyleConstants.setFontSize(keyWord, 14);
			StyleConstants.setAlignment(keyWord, StyleConstants.ALIGN_CENTER);
			StyleConstants.setBold(keyWord, true);
			
			SimpleAttributeSet keyWord1 = new SimpleAttributeSet();
			
			StyleConstants.setFontSize(keyWord1, 14);
			StyleConstants.setBold(keyWord1, true);
			
			SimpleAttributeSet keyWord2 = new SimpleAttributeSet();
			
			StyleConstants.setFontSize(keyWord2, 11);
			
			SimpleAttributeSet keyWord3 = new SimpleAttributeSet();
			
			StyleConstants.setFontSize(keyWord3, 14);
			
			doc.insertString(0,"Bun venit la Sus-Jos, versiunea cu întrebări de cultură generală!",keyWord);
			doc.insertString(doc.getLength(),"\n\n Reguli:",keyWord1);
			
			doc.insertString(doc.getLength(),"\n - Fiecare jucător are la dispoziţie un pion cu care se mişcă pe planşa de joc"
					+ " în sensul cifrelor aflate pe pătrate.\n - La fiecare mutare, jucătorul apasă butonul Întrebare Nouă "
					+ "iar pe ecran va apărea o întrebare la care acesta trebuie să răspundă, însă fereastra întrebării rămâne pe ecran doar 15 secunde"
					+ ". Dacă răspunde corect, acesta"
					+ " va avansa un pătrat.\n - Pătratele colorate de pe planşa de joc au rol de scurtătură, astfel că odata ajuns pe un"
					+ " pătrat verde sau albastru, va apărea o întrebare auxiliară la care răspunsul corect te saltă până la următorul"
					+ " pătrat de aceeaşi culoare. Pătratele roz au efect negativ, astfel că dacă răspunsul la întrebarea "
					+ "bonus este greşit, vei fi transportat înapoi.\n - Jocul se termină atunci când un jucător a ajuns pe pătratul "
					+ "final. Pentru a reîncepe, apasă butonul Reîncepe.\n",keyWord2);
			
			doc.insertString(doc.getLength(),"-----------------------------------------------------\n",keyWord1);
			if(!iswithComputer()) 
				{
				doc.insertString(doc.getLength(),"Acum este rândul lui:", keyWord3);
				doc.insertString(doc.getLength(),""+ player.getPlayerName() , keyWord1); 
				}
																									// Scriem randul fiecarui jucator, metoda urmand sa fie apelata la fiecare runda.
			
	}
	
	public int getRound(){																			// setter si getter pentru runda.
    	return round;
    }
	public void setRound(int x){
		round = x;
    }
	public boolean isFinished(){																	// modificarea parametrului boolean care indica sfarsitul jocului.
    	return finished;
    }
	public void setnotFinished(boolean x){
		finished = x;
    }
	public boolean iswithComputer(){																	// modificarea parametrului boolean care indica sfarsitul jocului.
    	return withComputer;
    }

}

class BoardPanel extends JPanel {																	// Clasa ce instantiaza plansa de joc.
	 
	private static final long serialVersionUID = 1L;
	
	private Image board;
	private Player player1;
	private Player player2;
	
    public BoardPanel(Image img, Player player1, Player player2) {									// Constructorul ei ce primeste ca parametrii imaginea plansei si cei 2 jucatori
    	
      this.player1 = player1;
      this.player2 = player2;
	  board = img;
	  Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));						// Setam dimensiunea plansei.
	  setPreferredSize(size);
	  setMinimumSize(size);
	  setMaximumSize(size);
	  setSize(size);
	  setLayout(null);
    }
    
    public void paintComponent(Graphics g) {														// Metoda prin care "improspatam" imaginile de pe ecran, repictand imaginile plansei si a pionilor jucatoriilor in functie de locatia acestora, de fiecare data cand invocam metoda repaint(); 
      g.drawImage(board, 0, 0, null);
      
      g.drawImage(player1.getpawnImage(), player1.getpawnlocation().x, player1.getpawnlocation().y ,null);
      g.drawImage(player2.getpawnImage(), player2.getpawnlocation().x, player2.getpawnlocation().y ,null);
    }
    
  }

class UserInterface extends JPanel{																	// Clasa ce instantiaza interfata cu butoane

	private static final long serialVersionUID = 1L;

	public UserInterface(BoardImpl board, BoardPanel panel, Player player1, Player player2, int round, JTextPane outputpanel){
		
	this.setSize(290, 625);
	
	QuestionButton questionbutton = new QuestionButton("Întrebare Nouă.", board, panel, player1, player2, outputpanel);
	
	questionbutton.setLocation(600, 600);
	questionbutton.setSize(135, 50);
	add(questionbutton);
	
	ResetButton resetbutton = new ResetButton("Reîncepe.", board, panel, player1, player2);
	
	resetbutton.setLocation(135, 600);
	resetbutton.setSize(135, 50);
    add(resetbutton);
	}
	
}

class QuestionButton extends JButton implements ActionListener{										// Butonul de intrebare noua, locul in care se intampla majoritatea actiunilor in jocul nostru.
									
		private static final long serialVersionUID = 15;
																									// De fiecare data cand butonul va fi "activat", o intrebare va aparea iar in functie de raspuns, plansa se va schimba
		BoardImpl board;
		BoardPanel panel;																			// Din aceasta cauza, avem nevoie de majoritatea parametrilor, cei doi jucatori, plansa si fereastra.
		Player player1;
		Player player2;
		JTextPane outputpanel;
		
		public QuestionButton(String s, BoardImpl board, BoardPanel panel, Player player1, Player player2, JTextPane outputpanel){
			super(s);
			this.board = board;																		
			this.panel = panel;
			this.player1 = player1;
			this.player2 = player2;
			this.setPreferredSize(new Dimension(135,50));
			this.outputpanel = outputpanel;
			addActionListener(this);																		// Implementam Interfata ce ne leaga de activitatea butonului.
		}																									// Adica butonului nostru trebuie sa i se spuna ca EXISTA ceva de facut, inainte de a i se da instructiuni

		@Override
		public void actionPerformed(ActionEvent e){														// Iar prin metoda aceasta ii indicam butonului ce are de facut atunci cand e apasat.
			if(!board.isFinished()){
				
				if(board.iswithComputer())																	// Daca jucam cu calculatorul
					
					{																						// In functie de runda, apoi in functie de raspunsul la intrebare, mutam jucatorul al carui rand a venit, si schimbam runda imediat dupa.
						try {
							new QuestionFrame(board,panel,player1,player2, false, true).startQFrame();
																											// Afisam panoul intrebarii.
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						
					}
				else
					if(board.getRound()==0){																// In functie de runda, apoi in functie de raspunsul la intrebare, mutam jucatorul al carui rand a venit, si schimbam runda imediat dupa.
						try {
							new QuestionFrame(board,panel,player1,player2, false, true).startQFrame();
																											// Afisam panoul intrebarii.
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						board.setRound(1);																	// Schimbam runda.
						try {
							board.writeOnTextPanel(outputpanel, player2);									// Improspatam panoul de text.
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						
						outputpanel.revalidate();
						outputpanel.repaint();
					}
					else{
						try {
							new QuestionFrame(board,panel,player2,player1, false, true).startQFrame();
								
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						board.setRound(0);
						try {
							board.writeOnTextPanel(outputpanel, player1);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						outputpanel.revalidate();
						outputpanel.repaint();
					}
			}
			else																						//Daca jocul s-a terminat, anuntam castigatorul.
				JOptionPane.showMessageDialog(null, "Joc terminat. " + getWinner(player1,player2) + " a castigat!");
			}
		
		public String getWinner(Player player1, Player player2){										// Verificam care jucator a ajuns mai departe si returnam numele lui.
			String s ="error";
			if(player1.getsquareCounter()>player2.getsquareCounter())
				s = player1.getPlayerName();
			else
				s = player2.getPlayerName();
			return s;
			}
	}

class ResetButton extends JButton implements ActionListener{											// Clasa ce intantiaza butonul de resetare a jocului
	
	private static final long serialVersionUID = 15;

	BoardImpl board;
	BoardPanel panel;
	Player player1;
	Player player2;
	
	public ResetButton(String s, BoardImpl board, BoardPanel panel, Player player1, Player player2){
		super(s);
		this.board = board;
		this.panel = panel;
		this.player1 = player1;
		this.player2 = player2;
		this.setPreferredSize(new Dimension(135,50));
		
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {											// Actiunea pe care o implementeaza e aceea de a reveni la coordonatele initiale, re a reseta contoarele de patrate a fiecarui jucator si de a repicta ecranul.
		 player1.setPawnlocation(new Point(20, 545));
		 player2.setPawnlocation(new Point(20, 505));
		 player1.setsquareCounter(0);
		 player2.setsquareCounter(0);
		 panel.repaint();
		 board.setnotFinished(false);
	}
	}

class Player{																				// Clasa ce instantiaza un jucator.
	
	private Image pawn = new ImageIcon("src/red_pawn.png").getImage();						// Fiecare jucator are o imagine a pionului sau, precum si un punct in planul de coordonate al plansei.
	private Point location = new Point(25, 595);
	private int squareCounter = 0;															// De asemenea, creeam un contor pentru patratele parcurse si un nume al jucatorului.
	private String name;
	private Color color;
	private boolean isComputer = false;
	
	public Player(int x, int y, String pname, Color color){									// Constructorul ce instantiaza un jucator nou cu o locatie, imagine si nume diferit			
		location.x = x;
		location.y = y;
		name = pname;
		this.color = color;
	}
	
	public Point getpawnlocation(){															// Mai jos avem metodele aferente pentru modificarea si redarea parametrilor jucatorilor
	    	return location;
	    }
	public void setPawnlocation(Point x){
	    	location = x;
	    }
	public Image getpawnImage(){
		return pawn;
	}
	public void setpawnImage(Image x){
		pawn = x;
	}
	public int getsquareCounter(){
    	return squareCounter;
    }
	public void setsquareCounter(int x){
		squareCounter = x;
    }
	public String getPlayerName(){
    	return name;
    }
	public void setplayerName(String x){
		name = x;
    }
	public void setColor(Color x){
		color = x;
    }
	public Color getColor(){
    	return color;
    }	
	public void setisComputer(boolean x){
		isComputer = x;
    }
	public boolean getisComputer(){
    	return isComputer;
    }
}


class UserInputFrame extends JFrame {														// Clasa ce instantiaza fereastra cu care incepe programul, in care se introduc detaliile jucatorilor.
	
	private static final long serialVersionUID = 1L;
	Player player1;
	Player player2;
	BoardImpl board;
	public UserInputFrame(Player player1, Player player2,BoardImpl board1){
		
	board = board1;
	this.player1 = player1;
	this.player2 = player2;
	}

	public void startUIFrame(Player player1, Player player2) throws IOException{

		
		JPanel container = new JPanel();													// Cream rama, textul, butoanele cu culori, casetele de text si butonul de mers mai departe si le pozitionam.
		container.setLayout(null);
		
		JLabel label = new JLabel("Vă rugăm să selectaţi numele şi culoare pionului pentru fiecare jucător.", JLabel.CENTER);
		
		label.setSize(545, 30);
        label.setLocation(0, 20);

        JLabel p1label = new JLabel("Jucător 1:", JLabel.CENTER);
		
		p1label.setSize(100, 30);
        p1label.setLocation(50, 70);
        
        JLabel p1name = new JLabel("nume:");
		
        p1name.setSize(100, 30);
        p1name.setLocation(22, 100);
        
        JLabel p1color = new JLabel("culoarea pionului:");
		
        p1color.setSize(100, 30);
        p1color.setLocation(22, 150);
        
        JEditorPane p1panel = new JEditorPane();
        
        p1panel.setSize(150,20);
		p1panel.setLocation(70,105);
        
        JLabel p2label = new JLabel("Jucător 2 / Calculator :", JLabel.CENTER);
		
		p2label.setSize(140, 30);
        p2label.setLocation(253, 70);
		
        JEditorPane p2panel = new JEditorPane();
        
        p2panel.setSize(150,20);
		p2panel.setLocation(260,105);
		
		ColorButton red1 = new ColorButton(player1, Color.RED);
		red1.setSize(30, 30);
        red1.setLocation(70,185);
        
		ColorButton blue1 = new ColorButton(player1, Color.BLUE);
		blue1.setSize(30, 30);
		blue1.setLocation(110,185);
        
		ColorButton green1 = new ColorButton(player1, Color.GREEN);
		green1.setSize(30, 30);
		green1.setLocation(150, 185);
        
		ColorButton yellow1 = new ColorButton(player1, Color.YELLOW);
		yellow1.setSize(30, 30);
		yellow1.setLocation(190, 185);
		
		ColorButton red2 = new ColorButton(player2, Color.RED);
		red2.setSize(30, 30);
		red2.setLocation(260,185);
        
		ColorButton blue2 = new ColorButton(player2, Color.BLUE);
		blue2.setSize(30, 30);
		blue2.setLocation(300,185);
        
		ColorButton green2 = new ColorButton(player2, Color.GREEN);
		green2.setSize(30, 30);
		green2.setLocation(340, 185);
        
		ColorButton yellow2 = new ColorButton(player2, Color.YELLOW);
		yellow2.setSize(30, 30);
		yellow2.setLocation(380, 185);
        
        JButton PVP = new OKButton("Jucător vs. Jucător.", p1panel, board, this, player1, player2, p1panel, p2panel,false);
        PVP.setSize(170, 50);
        PVP.setLocation(450, 100);
		
        JButton PVC = new OKButton("Jucător vs. Calculator.", p1panel, board, this, player1, player2, p1panel, p2panel,true);
        PVC.setSize(170, 50);
        PVC.setLocation(450, 170);
        
        container.add(p1panel);																//Le adaugam pe fiecare in rama.
        container.add(p2panel);
		container.add(label);
		container.add(p1label);
		container.add(p2label);
		container.add(p1name);
		container.add(p1color);
		container.add(p1label);
		container.add(p1label);
		container.add(p1label);
		container.add(PVP);
		container.add(PVC);
		container.add(red1);
		container.add(blue1);
		container.add(yellow1);
		container.add(green1);
		container.add(red2);
		container.add(blue2);
		container.add(yellow2);
		container.add(green2);
		
		setContentPane(container);
		setLocationByPlatform(true);
	    setVisible(true);																			
	    setTitle("Game");
	    setSize(650,280);

	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}

class OKButton extends JButton implements ActionListener{									// Butonul ce face trimiterea la plansa de joc, odata ce parametrii au fost alesi.
	
	private static final long serialVersionUID = 15;
	BoardImpl board;
	Player player1;
	Player player2;
	UserInputFrame frame;
	JEditorPane p1panel;
	JEditorPane p2panel;
	boolean withComputer;
	
	public OKButton(String s, JEditorPane panel, BoardImpl board1, UserInputFrame frame, 
			Player player1, Player player2, JEditorPane p1panel, JEditorPane p2panel, boolean withComputer){
		super(s);
		this.frame = frame;
		this.player1 = player1;
		this.player2 = player2;
		board = board1;
		addActionListener(this);
		this.p1panel = p1panel;
		this.p2panel = p2panel;
		this.withComputer = withComputer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {											// Cand apasam butonul, preluam textul din casete si il atribuim ca nume jucatorilor.
		
		player1.setplayerName(p1panel.getText());
		player2.setplayerName(p2panel.getText());											//Daca culorile sunt diferite si numele nu sunt goalte, mergem mai departe.
		player2.setisComputer(true);
		
		if(player1.getColor()==Color.white || player2.getColor()==Color.white || player2.getPlayerName().length()==0 || player1.getPlayerName().length()==0)
			{
			JOptionPane.showMessageDialog(null, "Alege o culoare şi un nume diferite pentru ambii jucători!", "Ai grijă!", 0, null);
			}
		else
		{
		if(player1.getColor()==player2.getColor())
		{
			JOptionPane.showMessageDialog(null, "Alege culori diferite pentru ambii jucători.", "Ai grijă!", 0, null);
		}
		else
		{
		try {
			frame.dispose();																// Atribuim fiecarui jucator imaginea respectiva culorii alese si pornim plansa.
			player1.setpawnImage(getplayerImage(board, player1));
			player2.setpawnImage(getplayerImage(board, player2));
			board.start(board, player1, player2, withComputer);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		}
	}
	}
	public Image getplayerImage(BoardImpl board, Player player){
		Image img = new ImageIcon("red_pawn.png").getImage();
		if(player.getColor()==Color.GREEN){
			try {
				img = ImageIO.read(board.getClass().getResource("green_pawn.png"));
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		if(player.getColor()==Color.RED){
			try {
				img = ImageIO.read(board.getClass().getResource("red_pawn.png"));
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		if(player.getColor()==Color.YELLOW){
			try {
				img = ImageIO.read(board.getClass().getResource("yellow_pawn.png"));
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		if(player.getColor()==Color.BLUE){
			try {
				img = ImageIO.read(board.getClass().getResource("blue_pawn.png"));
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		return img;
	}
}

class ColorButton extends JButton implements ActionListener{								//Butonul ce instantiaaz alegerea culorii
	
	private static final long serialVersionUID = 15;

	Player player;
	Color color;
	
	public ColorButton(Player player, Color newcolor){
	
		this.player = player;
		color = newcolor;
		this.setBackground(color);
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {											//Atunci cand e apasat, schimba culoarea jucatorului cu cea pe care el o reprezinta.
		player.setColor(color);
	}

}

class QuestionFrame extends JFrame implements ActionListener{								//Clasa ce instantiaza fereastra intrebarii.
	
	private static final long serialVersionUID = 1L;

	private BoardImpl board;
	boolean timesup = true;
	Player player1;
	Player player2;
	BoardPanel panel;
	Boolean special;
	Timer timer;
	String[] question;																		// Aducem o noua intrebare prin metoda popQuestion() definita anterior.
																							// Pentru a instantia butoanle de raspuns in mod corect, vedem in ce fel de casunta suntem
    boolean corectButton;																	// Cream o variabila care indica daca intrebarea e una benefica sau una rea
																							// Daca e o intrebare rea, inseamna ca raspunsul corect e cel care determina mentinerea pozitiei, iar raspunsul gresit e cel ce misca pionul.
    AnswerButton a1;																		// Cream cele 4 butoane cu raspunurile la intrebari.
    AnswerButton a2;																		// Butonul 1 va fi tot timpul cel corect, dar pozitia lui va fi amestecata.
    AnswerButton a3;
    AnswerButton a4 ;
 
	public QuestionFrame(BoardImpl board, BoardPanel panel, Player player1, Player player2, Boolean special, boolean corectButton) throws IOException{
		
		this.board = board;
		this.panel = panel;
		this.player1 = player1;
		this.player2 = player2;
		this.special = special;
		
		 question = board.popQuestion(board);																		//Aducem o noua intrebare prin metoda popQuestion() definita anterior.
									
	     this.corectButton = corectButton;																			// Cream o variabila care indica daca intrebarea e una benefica sau una rea
	     																											// Daca e o intrebare rea, inseamna ca raspunsul corect e cel care determina mentinerea pozitiei, iar raspunsul gresit e cel ce misca pionul.
	     a1 = new AnswerButton(question[1],corectButton, this, player1, player2, board, panel, special);			// Cream cele 4 butoane cu raspunurile la intrebari.
	     a2 = new AnswerButton(question[2],!corectButton, this, player1, player2, board, panel, special);			// Butonul 1 va fi tot timpul cel corect, dar pozitia lui va fi amestecata.
	     a3 = new AnswerButton(question[3],!corectButton, this, player1, player2, board, panel, special);
	     a4 = new AnswerButton(question[4],!corectButton, this, player1, player2, board, panel, special);
		
	}
	
	public void startQFrame() throws IOException{											// Atunci cand butonul Intrebare Noua este apasat, afisam fereastra.
		

		timer = new Timer(15000,this);														// Cream un cronometru care va inchide fereastra dupa 15 secunde.
		
		timer.start();

						
	
		JPanel container = new JPanel();
		container.setLayout(null);
		
		JLabel label = new JLabel(question[0], JLabel.CENTER);								// Afisam intrebarea.
		
		label.setSize(820, 30);
        label.setLocation(5, 20);
        
        int[][] buttonpositions = new int[][]{ {100 , 100 , 450 , 450},						// Cream o matrice cu pozitiile fiecarui buton
                                               {70 , 150, 70 ,  150}};
        int i = (int)(Math.random()*4);														// Cream o cifra in mod aleator, intre 1 si 4
        a1.setSize(270,50);
        a2.setSize(270,50);
        a3.setSize(270,50);
        a4.setSize(270,50);
        if(i>3){ 																			// In functie de cifra, pozitionam cele 4 butoane.
        	i = 0;
        	a4.setLocation(buttonpositions[0][i], buttonpositions[1][i]);	
        }
        else a4.setLocation(buttonpositions[0][i], buttonpositions[1][i]);
        i++;
        
        if(i>3){ 
        	i = 0;
        	a1.setLocation(buttonpositions[0][i], buttonpositions[1][i]);	
        }
        else a1.setLocation(buttonpositions[0][i], buttonpositions[1][i]);
        i++;
        
        if(i>3){ 
        	i = 0;
        	a2.setLocation(buttonpositions[0][i], buttonpositions[1][i]);	
        }
        else a2.setLocation(buttonpositions[0][i], buttonpositions[1][i]);
        i++;
        
        if(i>3){ 
        	i = 0;
        	a3.setLocation(buttonpositions[0][i], buttonpositions[1][i]);	
        }
        else a3.setLocation(buttonpositions[0][i], buttonpositions[1][i]);
        i++;
        
        container.add(label);																		// Adaugam cele 4 butoane si intrebarea in fereastra.
        container.add(a1);
        container.add(a2);
        container.add(a3);
        container.add(a4);
        
		setContentPane(container);
	    setVisible(true);																			
	    setTitle("Intrebare");
	    setSize(820,280);
	    setResizable(false);
	    setDefaultCloseOperation(HIDE_ON_CLOSE);
	    
	}

	@Override
	public void actionPerformed(ActionEvent e) {													// Atunci cand cele 15 secunde au expirat, inchidem fereastra si instiintam jucatorul.
		
		if(timesup)JOptionPane.showMessageDialog(board, "Timpul alocat întrebării a expirat.", "Avertisment", 0);
		timer.stop();
		this.dispose();
		
	}
}

class AnswerButton extends JButton implements ActionListener{										// Clasa	ce instantiaza butonul de raspuns.			
	
	private static final long serialVersionUID = 15;

	boolean corect = false;
	QuestionFrame frame;
	Player player1;
	Player player2;
	BoardImpl board;
	BoardPanel panel;
	Boolean specialmove;
	
	
	public AnswerButton(String s, boolean corect, QuestionFrame frame, Player player1, Player player2, BoardImpl board, BoardPanel panel, Boolean special ){
		super(s);
		specialmove=special;
		this.board = board;
		this.panel = panel;
		this.player1 = player1;
		this.player2 = player2;
		this.corect = corect;
		this.setPreferredSize(new Dimension(135,50));
		this.frame = frame;
		
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {												// Daca e o intrebare normala, apelam medota care muta doar un patratel
		frame.dispose();
		frame.timesup = false;
		if(corect)
			if(!specialmove)
			{
				movePlayer(player1, player2);		
			}
			else																				// Daca e o intrebare pentru scurtatura, apelam metoda ce muta pionul in functie de scurtatura.
				specialmovePlayer(player1, player2);
																								//Odata ce un buton a fost apasat, inchidem fereastra intrebarii.
		
		if(!specialmove && board.iswithComputer())
		{
			int randomnr = (int)(Math.random()*2);
			
			if(randomnr==0)
			{
			JOptionPane.showMessageDialog(null, "Calculatorul a răspuns corect la întrebare. Merge mai departe.");
			movePlayer(player2, player1);
			int aux = board.requiredNoSquares(player2.getsquareCounter());
			 
			 if(aux !=-100) 
			 {
				 
				 int nrandomnr = (int)(Math.random()*2);
					
				if(nrandomnr==0)
				{
					Point p2 = changeCoordinates(player2.getpawnlocation().x, player2.getpawnlocation().y);		// instantiem un punct in plan cu urmatoarea pozitie la care jucatorul e fata trebuie sa ajunga
					Point p1 = player1.getpawnlocation();														// de asemenea, instantiem un punct cu coordonatele acutale ale celui de-al doilea jucator

					// salvam in variabila auxiliara rezultatul metodei de parcurgere a plansei, cea care indica daca patratul actual este unul "special" sau nu.
					// Daca este un patrat special, repictam plansa si afisam o intrebare auxiliara care determina daca jucatorul va beneficia de scurtatura sau nu.
					  if(aux>0)
					  {
						  panel.repaint();
						  JOptionPane.showMessageDialog(null, "Calculatorul a răspuns corect la întrebarea bonus. Merge pe scurtătură.");
						  player2.setsquareCounter(player2.getsquareCounter() + aux);
							  for(int i=0; i < aux; i++){
								  
								  p2 = changeCoordinates(player2.getpawnlocation().x, player2.getpawnlocation().y);		// Schimbam coordonatele de atatea ori, cate patratele trebuie jucatorul sa sara.
								  
								  player2.setPawnlocation(p2); 
								  System.out.println(player2.getpawnlocation().toString());
							  
						  }
						  
					  }
					  else																								// La fel procem si pentru "scurtaturi nefavorabile", daca raspunsul la intrebare e gresit.
					  {
						  panel.repaint();
						  
							  player2.setsquareCounter(player2.getsquareCounter() + aux);
							  JOptionPane.showMessageDialog(null, "Calculatorul a răspuns greşit la întrebarea bonus. Acum face cale întoarsă.");
							  for(int i=0; i < Math.abs(aux); i++){
								  
								  p2 = reversechangeCoordinates(player2.getpawnlocation().x, player2.getpawnlocation().y);	// Invocam metoda inversa mersului inainte, de atate ori cat e necesar
								  
								  player2.setPawnlocation(p2); 
								  
							  }

						  panel.repaint();
					  }
					  panel.repaint();
					  
				  if(areSameSpot(player2,player1)) 											// Daca jucatorii sunt in acelasi loc, schimbam putin coordonatele astfel incat sa incapa amandoi in patratul respectiv
				  {
					  p1.y = p1.y - 20;
					  p2.y = p2.y + 20;
			  
					  player1.setPawnlocation(p2);
					  player2.setPawnlocation(p1);

				  }
				  else																		// La fel, daca nu sunt in acelasi loc, revenim la locatiile de centru ale patratelor pentru fiecare jucator
				  {
					  
					  p1.y = (p1.y/100)*100 + 25;
					  p2.y = (p2.y/100)*100 + 25;
					  player1.setPawnlocation(p1);
					  player2.setPawnlocation(p2);
				  }
				  
				  panel.repaint();															//repictam
			    }
				else JOptionPane.showMessageDialog(null, "Calculatorul a răspuns la întrebarea bonus. Rămâne pe loc.");
			 }
			 }
			else  JOptionPane.showMessageDialog(null, "Calculatorul a răspuns greşit la întrebare. Nu merge mai departe.");
		}
		
		
		}
	
	


public void movePlayer(Player player1, Player player2){											// Medota care muta primul jucator care il primeste, si in functie de pozitia acestuia, muta pozitia celui de-al doilea jucator, daca sunt pe acelasi patrat

  {																								// Daca nici unul din jucatori nu e la final, incepe repozitionarea
	    
	  Point p1 = changeCoordinates(player1.getpawnlocation().x, player1.getpawnlocation().y);	// instantiem un punct in plan cu urmatoarea pozitie la care jucatorul e fata trebuie sa ajunga
	  Point p2 = player2.getpawnlocation();														// de asemenea, instantiem un punct cu coordonatele acutale ale celui de-al doilea jucator

	  player1.setPawnlocation(p1);																// schimbam pozitia primului jucator
	  player1.setsquareCounter(player1.getsquareCounter()+1);
	  
	  int aux = board.requiredNoSquares(player1.getsquareCounter());							// salvam in variabila auxiliara rezultatul metodei de parcurgere a plansei, cea care indica daca patratul actual este unul "special" sau nu.
	  //System.out.println(player1.getPlayerName() + aux);
	  if(aux!=-100)																				// Daca patratul nu este special, crestem contorul patratelor parcurse de jucatorul de fata																					// Daca este un patrat special, repictam plansa si afisam o intrebare auxiliara care determina daca jucatorul va beneficia de scurtatura sau nu.
			  try {
					if(!board.iswithComputer())
					{
						boolean x=true;
						if(aux!=-100 && aux<0)	x = false;
						new QuestionFrame(board,panel,player1,player2, true, x).startQFrame();			// Daca patratul este special, pornim o noua fereastra de intrebare, de data asta cu un caracter special, astfel ca metoda specialmovePlayer() va fi utilizata.
					}
					else 
						if(!player1.getisComputer())
						{
							boolean x=true;
							if(aux!=-100 && aux<0)	x = false;
							new QuestionFrame(board,panel,player1,player2, true, x).startQFrame();			// Daca patratul este special, pornim o noua fereastra de intrebare, de data asta cu un caracter special, astfel ca metoda specialmovePlayer() va fi utilizata.
						}
					
				} catch (IOException e2) {e2.printStackTrace();}
	  			
	  if(areSameSpot(player1,player2)) 																		// Daca jucatorii sunt in acelasi loc, schimbam putin coordonatele astfel incat sa incapa amandoi in patratul respectiv
	  {
		  p1.y = p1.y - 20;
		  p2.y = p2.y + 20;
  
		  player1.setPawnlocation(p2);
		  player2.setPawnlocation(p1);

	  }
	  else																									// La fel, daca nu sunt in acelasi loc, revenim la locatiile de centru ale patratelor pentru fiecare jucator
	  {
		  
		  p1.y = (p1.y/100)*100 + 25;
		  p2.y = (p2.y/100)*100 + 25;
		  player1.setPawnlocation(p1);
		  player2.setPawnlocation(p2);
	  }
	  
	  panel.repaint();																						//repictam
    }
  
  
  
  if(!(player1.getsquareCounter()<35 && player2.getsquareCounter()<35)) 																		// Acest "ELSE" este conectat de primul if, acela ce verifica daca jocul sa terminat sau nu.
	  {
		 panel.repaint();
		 board.setnotFinished(true);
		  JOptionPane.showMessageDialog(null, "Joc terminat. " + getWinner(player1,player2) + " a castigat!");
	  }
}

public void specialmovePlayer(Player player1, Player player2){												// Medota care muta primul jucator care il primeste, in mod special

		    
		  Point p1 = changeCoordinates(player1.getpawnlocation().x, player1.getpawnlocation().y);			// instantiem un punct in plan cu urmatoarea pozitie la care jucatorul e fata trebuie sa ajunga
		  Point p2 = player2.getpawnlocation();																// de asemenea, instantiem un punct cu coordonatele acutale ale celui de-al doilea jucator

		  int aux = board.requiredNoSquares(player1.getsquareCounter());									// salvam in variabila auxiliara rezultatul metodei de parcurgere a plansei, cea care indica daca patratul actual este unul "special" sau nu.
		//  System.out.println("specialmoved computer" + aux);																				// Daca este un patrat special, repictam plansa si afisam o intrebare auxiliara care determina daca jucatorul va beneficia de scurtatura sau nu.
			  if(aux>0)
			  {
				  panel.repaint();
				  
					  player1.setsquareCounter(player1.getsquareCounter() + aux);
					  for(int i=0; i < aux; i++){
						  
						  p1 = changeCoordinates(player1.getpawnlocation().x, player1.getpawnlocation().y);		// Schimbam coordonatele de atatea ori, cate patratele trebuie jucatorul sa sara.
						  
						  player1.setPawnlocation(p1); 
						  System.out.println(player1.getpawnlocation().toString());
					  
				  }
				  
			  }
			  else																								// La fel procem si pentru "scurtaturi nefavorabile", daca raspunsul la intrebare e gresit.
			  {
				  panel.repaint();
				  
					  player1.setsquareCounter(player1.getsquareCounter() + aux);
					  
					  for(int i=0; i < Math.abs(aux); i++){
						  
						  p1 = reversechangeCoordinates(player1.getpawnlocation().x, player1.getpawnlocation().y);	// Invocam metoda inversa mersului inainte, de atate ori cat e necesar
						  
						  player1.setPawnlocation(p1); 
						  
					  }

				  panel.repaint();
			  }
			  panel.repaint();
			  
		  if(areSameSpot(player2,player1)) 												// Daca jucatorii sunt in acelasi loc, schimbam putin coordonatele astfel incat sa incapa amandoi in patratul respectiv
		  {
			  p1.y = p1.y - 20;
			  p2.y = p2.y + 20;
	  
			  player1.setPawnlocation(p2);
			  player2.setPawnlocation(p1);

		  }
		  else																			// La fel, daca nu sunt in acelasi loc, revenim la locatiile de centru ale patratelor pentru fiecare jucator
		  {
			  
			  p1.y = (p1.y/100)*100 + 25;
			  p2.y = (p2.y/100)*100 + 25;
			  player1.setPawnlocation(p1);
			  player2.setPawnlocation(p2);
		  }
		  
		  panel.repaint();																//repictam
	    }
	

	public boolean areSameSpot(Player player1, Player player2){							// Medota ce verifica daca jucatorii sunt in acelasi patrat, folosindu-se de coordonatele x si y ale acestora.
		boolean ok = false;																// Presupunem ca jucatorii nu sunt in acelasi loc prin variabila ok.
		if(player2.getpawnlocation().x == player1.getpawnlocation().x 					// Verificam locatia fiecaruia, iar daca sunt in acelasi loc, schimbam ok in adevarat.
				&& player2.getpawnlocation().y == player1.getpawnlocation().y)
			ok = true;
		return ok;																		// Returnam valoarea lui ok.
		}
		
	public Point changeCoordinates( int x, int y){										// Medota care schimba coordonatele unui jucator, astfel ducandu-l in patratelul care urmeaza
		Point v = new Point(x,y);	// 20, 525
		
		if((v.y/100) % 2 == 1)															// Daca suntem pe o linie impara, mergem spre dreapta							
		{
			if(v.x < 500) 																	//Daca nu suntem la margine, schimbam pozitia orizontal
					v.x = v.x + 100;
			else
					v.y = v.y - 100;														//Altfel, inaintam vertical
		}
		else																			// Altfel, spre stanga
		{
			if(v.x > 100) 
				v.x = v.x - 100;
			else
				v.y = v.y - 100;
		}
		
		return v;																		// returnam pozitia
	
	}
	
	public Point reversechangeCoordinates( int x, int y){								// Metoda inversa, care te duce inapoi, special creata pentru "scurtaturile nefavorabile"
	Point v = new Point(x,y);	// 20, 525
	
	if((v.y/100) % 2 == 1)																// Mergem spre stanga
	{
		if(v.x > 100) 
				v.x = v.x - 100;														// orizontal
		else
				v.y = v.y + 100;														// vertical
	}
	else
	{
		if(v.x < 500) 
			v.x = v.x + 100;
		else
			v.y = v.y + 100;
	}
	
	return v;
	
	}
	
	public String getWinner(Player player1, Player player2){							// Verificam castigatorul.					
		String s ="error";
		if(player1.getsquareCounter()>player2.getsquareCounter())
			s = player1.getPlayerName();
		else
			s = player2.getPlayerName();
		return s;
		}
	}