import java.util.*;
import java.text.*;
import java.io.*;

import java.awt.Dimension;
//import java.awt.Frame;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionAdapter;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
//import javax.media.opengl.awt.*;
//import javax.media.opengl.GL;
//import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLEventListener;
//import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
//import com.jogamp.opengl.util.Animator;
//import com.jogamp.opengl.util.gl2.GLUT;

//import com.jogamp.opengl.util.*;
//import com.jogamp.opengl.util.gl2.GLUT;

import javax.swing.*;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JSplitPane;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.JMenu;

import java.awt.BorderLayout;

public class Wirecylinder implements GLEventListener {
    // , MouseListener, MouseMotionListener {
    //public class Wirecylinder implements GLEventListener {
    private int WIDTH = 1024;
    private int HEIGHT = 768;
    private float CAMERA = -10.0f;

    private int INFOWIDTH = 300;
    private int INFOHEIGHT = 300;

    // 単位
    private float UNIT = 1.0f;

    private GL gl;
    private GLU glu;
    private GLUT glut;
    private Animator animator;
    private GLCanvas canvas;
   
    private int prevMouseX;
    private int prevMouseY;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private float angleZ = 0.0f;
    private float distanceX = 0.0f;
    private float distanceY = 0.0f;

    //回転マトリックス
    //    private double rotate[];

    //クオータニオン
    //    private Quaternion target;
    //    private Quaternion current;

    // 変換行列
    private double[] m;

    // constants
    //    private double S1001 = 3650; // S of 1001keV
    //    private double S766 = 1282; // S of 766keV
    private double S1001 = 104.148747; // S of 1001keV
    private double S766 = 36.582714; // S of 766keV
    private double MURHO1001 = 0.0595; // mu/rho of 1001keV
    private double MURHO766 = 0.0678; // mu/rho of 1001keV
    private double AIRDENSITY = 0.0012d; // air density

    // drum can size
    private double drumheight = 0.89d; // outer size?
    private double drumdiam = 0.5692d; // outer size
    private double drumthick = 0.0016d;
    private double drumdensity = 7.874d;

    private ArrayList<Poligon> drumPoligonList;


    // pipe list
    private ArrayList<Pipe> pipeList;

    // detector 
    private Detector detector;

    // diameter to be shown on display
    private double diam = 0.0d;

    private int spnum = 0;

    // buttons
    JButton button = null;
    JButton removeButton = null;
    JButton plusXButton = null;
    JButton minusXButton = null;
    JButton plusYButton = null;
    JButton minusYButton = null;
    JButton plusZButton = null;
    JButton minusZButton = null;

    JButton plusAngleXButton = null;
    JButton minusAngleXButton = null;
    JButton plusAngleYButton = null;
    JButton minusAngleYButton = null;
    JButton plusAngleZButton = null;
    JButton minusAngleZButton = null;

    JButton plusHeightButton = null;
    JButton minusHeightButton = null;
    JButton plusDiamButton = null;
    JButton minusDiamButton = null;
    JButton plusThickButton = null;
    JButton minusThickButton = null;

    JButton plusDensityButton = null;
    JButton minusDensityButton = null;

    JButton plusRAButton = null;
    JButton minusRAButton = null;
    JButton plusRAHeightButton = null;
    JButton minusRAHeightButton = null;

    JButton toggleRAButton = null;

    JButton plusDetButton = null;
    JButton minusDetButton = null;
    JButton plusDetHeightButton = null;
    JButton minusDetHeightButton = null;
    JButton plusDetDistButton = null;
    JButton minusDetDistButton = null;

    JLabel aboutPipeLabel = null;

    JButton statButton = null;
    JButton calcButton = null;

    JMenuItem menuOpenFile = null;

    JFrame frame;
    JMenuBar bar;
    
    public Wirecylinder() {
	// initialize
	//	rotate = new double[16];
	//	target = new Quaternion();
	//	current = new Quaternion();
	//	current.x = 1.0;

	m = new double[16];

        //Frame frame = new Frame("Cylinder");
	frame = new JFrame("Cylinder");

	JFrame infoframe = new JFrame("Info");

        // 3Dを描画するコンポーネント
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);

	//        frame.add(canvas);
        frame.setSize(WIDTH, HEIGHT);
	infoframe.setSize(INFOWIDTH, INFOHEIGHT);

	Container contentPane = frame.getContentPane();
	Container infoContentPane = infoframe.getContentPane();

	// ラベルのインスタンスを生成
	aboutPipeLabel = new JLabel();
	aboutPipeLabel.setText("nothing is selected");
	// Split Pane
	JSplitPane spane = new JSplitPane();
	spane.setDividerLocation(200);  //分割された左領域を90px幅
	spane.setDividerSize(0);
	contentPane.add(spane, BorderLayout.CENTER);
	
	spane.setRightComponent(canvas);

	JPanel leftPanel = new JPanel();
	//	leftPanel.add(button, BorderLayout.SOUTH);
	//	leftPanel.setLayout(null);
	leftPanel.add(getbutton(),null);
	leftPanel.add(getRemoveButton(),null);

	leftPanel.add(getPlusXButton(),null);
	leftPanel.add(getMinusXButton(),null);
	leftPanel.add(getPlusYButton(),null);
	leftPanel.add(getMinusYButton(),null);
	leftPanel.add(getPlusZButton(),null);
	leftPanel.add(getMinusZButton(),null);

	leftPanel.add(getPlusAngleXButton(),null);
	leftPanel.add(getMinusAngleXButton(),null);
	leftPanel.add(getPlusAngleYButton(),null);
	leftPanel.add(getMinusAngleYButton(),null);
	leftPanel.add(getPlusAngleZButton(),null);
	leftPanel.add(getMinusAngleZButton(),null);

	leftPanel.add(getPlusHeightButton(),null);
	leftPanel.add(getMinusHeightButton(),null);
	leftPanel.add(getPlusDiamButton(),null);
	leftPanel.add(getMinusDiamButton(),null);
	leftPanel.add(getPlusThickButton(),null);
	leftPanel.add(getMinusThickButton(),null);

	leftPanel.add(getPlusDensityButton(),null);
	leftPanel.add(getMinusDensityButton(),null);

	leftPanel.add(getPlusRAButton(),null);
	leftPanel.add(getMinusRAButton(),null);
	//	leftPanel.add(getPlusRAHeightButton(),null);
	//	leftPanel.add(getMinusRAHeightButton(),null);

	//	leftPanel.add(getToggleRAButton(),null);

	leftPanel.add(getPlusDetButton(),null);
	leftPanel.add(getMinusDetButton(),null);
	leftPanel.add(getPlusDetHeightButton(),null);
	leftPanel.add(getMinusDetHeightButton(),null);
	leftPanel.add(getPlusDetDistButton(),null);
	leftPanel.add(getMinusDetDistButton(),null);

	leftPanel.add(getStatButton(),null);
	//	leftPanel.add(getCalcButton(),null);
	
	// add label

	infoContentPane.add(aboutPipeLabel,null);

	spane.setLeftComponent(leftPanel);


	// generate a new menu bar
	bar = new JMenuBar();

	// generate a menu
	JMenu menu0 = new JMenu("File");
	JMenu menu1 = new JMenu("Edit");

	//JMenuItem menuOpenFile = new JMenuItem("Open File...");
	//        menuSaveAs.addActionListener(new AbstractAction() {
	//            private static final long serialVersionUID = 1L;
	//            public void actionPerformed(ActionEvent e) {
	//                onSaveAs();
	//            }
	//        });
        
        // システムのデフォルトのコマンド修飾キーを取得する.
        // Windowsならctrl, OSXならばmetaになる.
        Toolkit tk = Toolkit.getDefaultToolkit();
        int shortCutKey = tk.getMenuShortcutKeyMask();

	JMenuItem menuCut = new JMenuItem("Cut");
	JMenuItem menuCopy = new JMenuItem("Copy");
	JMenuItem menuPaste = new JMenuItem("Paste");

	menuCut.setEnabled(false);
	menuCopy.setEnabled(false);
	menuPaste.setEnabled(false);

        menuCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortCutKey));
        menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortCutKey));
        menuPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, shortCutKey));


        menu0.add(getMenuOpenFile());

	menu1.add(menuCut);
	menu1.add(menuCopy);
	menu1.add(menuPaste);

        //メニューをメニューバーに表示
        bar.add(menu0);
	bar.add(menu1);
        frame.setJMenuBar(bar);

        // ラベルをContentPaneに配置
	//	contentPane.add(label);	
        // ボタンをContentPaneに配置
	
	// drum can poligon list
	drumPoligonList = new ArrayList<Poligon>();

	// Pipe List
	pipeList = new ArrayList<Pipe>();

	/*
	pipeList.add(new Pipe());
	pipeList.get(0).angleX = 10;
	pipeList.get(0).angleY = 90;
	pipeList.get(0).locationX = 0.2f;
	pipeList.get(0).hasRA = true;
	pipeList.get(0).raY = 0.4f;

	pipeList.add(new Pipe());
	pipeList.get(1).angleY = 110;
	pipeList.get(1).locationZ = (0.567f-0.1143f)/2.0f;
	pipeList.get(1).hasRA = true;
	pipeList.get(1).raY = -0.25f;
	*/

	pipeList.add(new Pipe());
	pipeList.get(pipeList.size()-1).sized = 0.2674f;
	pipeList.get(pipeList.size()-1).sizet = 0.0066f;
	pipeList.get(pipeList.size()-1).sizeh = 0.940f;
	pipeList.get(pipeList.size()-1).locationX = -0.2674f/2;

	pipeList.add(new Pipe());
	pipeList.get(pipeList.size()-1).sized = 0.2418f;
	pipeList.get(pipeList.size()-1).sizet = 0.0062f;
	pipeList.get(pipeList.size()-1).sizeh = 0.990f;
	pipeList.get(pipeList.size()-1).locationX = -0.2674f/2;

	pipeList.add(new Pipe());
	//	pipeList.get(pipeList.size()-1).addRALines(4, (float)(200/8/9), 0.2163f-(0.0058f*2), 0.7f, true);
	//	pipeList.get(pipeList.size()-1).addRALines(8, (float)(200/8/9), 0.2163f-(0.0058f*2), 0.7f, true);
	pipeList.get(pipeList.size()-1).addRALines(5, (float)(200/8/9), 0.2163f-(0.0058f*2), 0.7f, true);
	pipeList.get(pipeList.size()-1).angleY = 180;
	pipeList.get(pipeList.size()-1).sized = 0.2163f;
	pipeList.get(pipeList.size()-1).sizet = 0.0058f;
	pipeList.get(pipeList.size()-1).sizeh = 1.040f;
	pipeList.get(pipeList.size()-1).locationX = -0.2674f/2;

	pipeList.add(new Pipe());
	pipeList.get(pipeList.size()-1).sized = 0.2674f;
	pipeList.get(pipeList.size()-1).sizet = 0.0066f;
	pipeList.get(pipeList.size()-1).sizeh = 0.940f;
	pipeList.get(pipeList.size()-1).locationX = 0.2674f/2;

	pipeList.add(new Pipe());
	//	pipeList.get(pipeList.size()-1).addRALines(4, (float)(200/8/9), 0.2418f-(0.0062f*2), 0.7f, true);
	pipeList.get(pipeList.size()-1).addRALines(3, (float)(200/8/9), 0.2163f-(0.0058f*2), 0.7f, true);
	pipeList.get(pipeList.size()-1).sized = 0.2418f;
	pipeList.get(pipeList.size()-1).sizet = 0.0062f;
	pipeList.get(pipeList.size()-1).sizeh = 0.990f;
	pipeList.get(pipeList.size()-1).locationX = 0.2674f/2;

	// Detector
	detector = new Detector();

        canvas.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    prevMouseX = e.getX();
                    prevMouseY = e.getY();
                }
            });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    if (e.isShiftDown()) {
                        // 移動量の算出
                        float diffX = (float)(x - prevMouseX)/500.0f;
                        float diffY = (float)(prevMouseY - y)/500.0f;
		    
                        // 移動量の更新
			for (int i=0; i<pipeList.size(); i++){
			    if (pipeList.get(i).isSelected) {
				pipeList.get(i).locationX += diffX;
				pipeList.get(i).locationY += diffY;
				break;
			    }
			}
			//                        distanceX += diffX;
			//                        distanceY += diffY;
                    } else {	    
                        Dimension size = e.getComponent().getSize();

                        // 回転量の算出
                        // ウィンドウの端から端までで、360度回転するようにする
                        float thetaY = 360.0f * ((float)(x-prevMouseX)/size.width);
                        float thetaX = 360.0f * ((float)(prevMouseY-y)/size.height);

                        // 角度の更新
                        angleX -= thetaX;
                        angleY += thetaY;
                    }

                    // 現在のマウスの位置を保存
                    prevMouseX = x;
                    prevMouseY = y;
                }
            });

        canvas.addKeyListener(new KeyListener() {

                public void keyTyped(KeyEvent e) {
		    char c = e.getKeyChar();

		    switch (c) {
		    case 'a': 
			if (spnum > 0) {
			    spnum--;
			    System.out.println("spnum: '" + spnum +"'\n");
			}
			break;
		    case 's': 
			if (spnum < 79) {
			    spnum++;
			    System.out.println("spnum: '" + spnum +"'\n");
			}
			break;
		    case 'z': 
			// System.out.println("key is typed: '" + c +"'\n");
			if (diam > -1000.0d) { diam = diam - 1.0d;}
			break;
		    case 'x': 
			// System.out.println("key is typed: 'x'\n");
			if (diam < 3.0d) {diam = diam + 1.0d;}
			break;
		    case 'c': 
			// System.out.println("key is typed: 'x'\n");
			boolean flag = false;
			for (int i=0; i<pipeList.size(); i++){
			    if (pipeList.get(i).isSelected) {
				flag = true;
				pipeList.get(i).isSelected = false;
				aboutPipeLabel.setText("nothing is selected.");
			    } else if (flag) {
				pipeList.get(i).isSelected = true;
				aboutPipeLabel.setText("<html>" + Integer.toString(i) + " is selected.<br>" + pipeList.get(i).getStatus());
				break;
			    }
			}
			if (!flag){ 
			    pipeList.get(0).isSelected = true; 
				aboutPipeLabel.setText("0 is selected");
				aboutPipeLabel.setText("<html>0 is selected.<br>" + pipeList.get(0).getStatus());
			}
			break;
		    default:break;
		    }
                }

                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                }


            });


        animator = new Animator(canvas);

        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    animator.stop();
                    System.exit(0);
                }
            });

        frame.setVisible(true);
        infoframe.setVisible(true);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
	gl = drawable.getGL();
	glu = new GLU();
        glut = new GLUT();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    }

    public void reshape(GLAutoDrawable drawable,
                        int x, int y,
                        int width, int height) {

        float left, right, bottom, top;
        float ratio = (float)height / (float)width;
	
	//	System.out.printf("%d, %d\n", width, height);
	left = -0.5f;
	right = 0.5f;
	bottom = left * ratio;
	top = right * ratio;

	gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(gl.GL_PROJECTION);
	gl.glLoadIdentity();
	gl.glFrustum(left, right, bottom, top, 5.0f, 1000.0f);
	//	gl.glFrustum(-0.5f, 0.5f, -ratio, ratio, 5.0f, 1000.0f);

	// below doesn't work properly. the aspect ratio doesn't be refreshed real time. 
	// sucks.
	//	glu.gluPerspective(30.0, (double) (width/height), 1.0, 1000.0);
	//	glu.gluLookAt(0.0,0.0,2.5+diam, //カメラの座標
	//		     0.0,0.0,0.0, // 注視点の座標
	//		     0.0,1.0,0.0); // 画面の上方向を指すベクトル

        gl.glMatrixMode(gl.GL_MODELVIEW);
	//        gl.glLoadIdentity();
	gl.glTranslatef(0.0f, 0.0f, CAMERA);
    }

    public void display(GLAutoDrawable drawable) {
	// get forcus on the OpenGL canvas
	// to enable to get key commands
	canvas.requestFocusInWindow();

        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glPushMatrix();

	zoomView();

	//	gl.glMultMatrixd(rotate,0);

        // マウスの移動量に応じて回転
	//        gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
	//        gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);
	float[] yy = {0, 1.0f, 0};
	float[] xx = {1.0f, 0, 0};
	FreeRotate(xx, angleX);
	FreeRotate(yy, angleY);

	//        draw drum can
	gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
	//	wirecylinder(0.567, 0.89);
	wirecylinder(drumdiam, drumheight, drumthick, drumPoligonList);

	// draw pipes
	for (int i=0; i<pipeList.size(); i++){
	    pipeList.get(i).display();
	}

	// draw detector
	detector.display();

        gl.glPopMatrix();


	/* for debugging
	gl.glBegin(gl.GL_LINE_STRIP);
	gl.glVertex3d(pipeList.get(0).wraX, pipeList.get(0).wraY, pipeList.get(0).wraZ);
	gl.glVertex3d(detector.wX[0], detector.wY[0], detector.wZ[0]);
	gl.glVertex3d(pipeList.get(1).wraX, pipeList.get(1).wraY, pipeList.get(1).wraZ);
	gl.glEnd();

	gl.glBegin(gl.GL_LINE_STRIP);
	gl.glVertex3d(pipeList.get(0).wraX, pipeList.get(0).wraY, pipeList.get(0).wraZ);
	gl.glVertex3d(detector.wX[1], detector.wY[1], detector.wZ[1]);
	gl.glVertex3d(pipeList.get(1).wraX, pipeList.get(1).wraY, pipeList.get(1).wraZ);
	gl.glEnd();

	gl.glBegin(gl.GL_LINE_LOOP);
	gl.glVertex3d(drumPoligonList.get(spnum).wv0[0],drumPoligonList.get(spnum).wv0[1],drumPoligonList.get(spnum).wv0[2]);
	gl.glVertex3d(drumPoligonList.get(spnum).wv1[0],drumPoligonList.get(spnum).wv1[1],drumPoligonList.get(spnum).wv1[2]);
	gl.glVertex3d(drumPoligonList.get(spnum).wv2[0],drumPoligonList.get(spnum).wv2[1],drumPoligonList.get(spnum).wv2[2]);
	gl.glEnd();

	gl.glBegin(gl.GL_LINE_LOOP);
	gl.glVertex3d(pipeList.get(0).poligonList.get(spnum).wv0[0],
		      pipeList.get(0).poligonList.get(spnum).wv0[1],
		      pipeList.get(0).poligonList.get(spnum).wv0[2]);
	gl.glVertex3d(pipeList.get(0).poligonList.get(spnum).wv1[0],
		      pipeList.get(0).poligonList.get(spnum).wv1[1],
		      pipeList.get(0).poligonList.get(spnum).wv1[2]);
	gl.glVertex3d(pipeList.get(0).poligonList.get(spnum).wv2[0],
		      pipeList.get(0).poligonList.get(spnum).wv2[1],
		      pipeList.get(0).poligonList.get(spnum).wv2[2]);
	gl.glEnd();

	gl.glBegin(gl.GL_LINE_LOOP);
	gl.glVertex3d(pipeList.get(1).poligonList.get(spnum).wv0[0],
		      pipeList.get(1).poligonList.get(spnum).wv0[1],
		      pipeList.get(1).poligonList.get(spnum).wv0[2]);
	gl.glVertex3d(pipeList.get(1).poligonList.get(spnum).wv1[0],
		      pipeList.get(1).poligonList.get(spnum).wv1[1],
		      pipeList.get(1).poligonList.get(spnum).wv1[2]);
	gl.glVertex3d(pipeList.get(1).poligonList.get(spnum).wv2[0],
		      pipeList.get(1).poligonList.get(spnum).wv2[1],
		      pipeList.get(1).poligonList.get(spnum).wv2[2]);
	gl.glEnd();
	*/
    }

    public void displaynew(GLAutoDrawable drawable) {

	gl.glMatrixMode(gl.GL_PROJECTION);
	gl.glLoadIdentity();
	//視野角,アスペクト比(ウィンドウの幅/高さ),描画する範囲(最も近い距離,最も遠い距離)
	glu.gluPerspective(30.0, (double)WIDTH / (double)HEIGHT, 1.0, 1000.0);

	gl.glMatrixMode(gl.GL_MODELVIEW);
	gl.glLoadIdentity();
	//視点の設定
	//	glu.gluLookAt(150.0,100.0,-200.0, //カメラの座標
	//		     0.0,0.0,0.0, // 注視点の座標
	//		     0.0,1.0,0.0); // 画面の上方向を指すベクトル
	//クォータニオンによる回転
	//	gl.glMultMatrixd(rotate,0);

	//wirecylinder(1.0, 1.0);
 
	// glut.glutSwapBuffers();
    }


    public void wirecylinder(double d, double h, double t, ArrayList<Poligon> p) {
	// draw cylinder with d as diameter (chokkei) of outer wall and h as hight, t thickness
	int i;
	double t1, t2;
	double hd, hh, ihd;
	hd = d / 2.0d; // outer half diamerter (hankei)
	hh = h / 2.0d; // half height

	ihd = hd - t; // inner hankei

	int DIVIDEMODEL = 160; // divide number
	int DIVIDEDRAW  = 40; // divide number

	double[] v0 = new double[3];
	double[] v1 = new double[3];
	double[] v2 = new double[3];
	double[] v3 = new double[3];

	p.clear();
	//	System.out.printf("%d", p.size());

	gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, m, 0);

	// outer side to draw
	for (i=0;i<DIVIDEDRAW;i++) {
	    //gl.glColor3d(1.0,1.0,1.0);
	    gl.glBegin(gl.GL_LINE_LOOP);
	    t1 = i*2*3.1415/DIVIDEDRAW + 3.1415/DIVIDEDRAW; // hanbun zurasu
	    t2 = (i+1)*2*3.1415/DIVIDEDRAW + 3.1415/DIVIDEDRAW;
	    //            gl.glNormal3d(Math.cos(t), 0.0, Math.sin(t));
	    v0[0] = hd*Math.cos(t1);
	    v0[1] = -hh;
	    v0[2] = hd*Math.sin(t1);

	    v1[0] = hd*Math.cos(t1);
	    v1[1] = hh;
	    v1[2] = hd*Math.sin(t1);

	    v2[0] = hd*Math.cos(t2);
	    v2[1] = hh;
	    v2[2] = hd*Math.sin(t2);

	    v3[0] = hd*Math.cos(t2);
	    v3[1] = -hh;
	    v3[2] = hd*Math.sin(t2);

	    // gl.glVertex3d(hd*Math.cos(t1), -hh, hd*Math.sin(t1));
            // gl.glVertex3d(hd*Math.cos(t1), hh, hd*Math.sin(t1));
            // gl.glVertex3d(hd*Math.cos(t2), hh, hd*Math.sin(t2));
            // gl.glVertex3d(hd*Math.cos(t2), -hh, hd*Math.sin(t2));
	    gl.glVertex3dv(v0,0);
	    gl.glVertex3dv(v1,0);
	    gl.glVertex3dv(v2,0);
	    gl.glVertex3dv(v3,0);
	    gl.glEnd();
	}

	// DEBUG
	//	System.out.printf("%d\n", p.size());

	// outer side, this won't be draw, just be added as poligons
	for (i=0;i<DIVIDEMODEL;i++) {
	    t1 = i*2*3.1415/DIVIDEMODEL + 3.1415/DIVIDEMODEL;
	    t2 = (i+1)*2*3.1415/DIVIDEMODEL + 3.1415/DIVIDEMODEL;

	    v0[0] = hd*Math.cos(t1);
	    v0[1] = -hh;
	    v0[2] = hd*Math.sin(t1);

	    v1[0] = hd*Math.cos(t1);
	    v1[1] = hh;
	    v1[2] = hd*Math.sin(t1);

	    v2[0] = hd*Math.cos(t2);
	    v2[1] = hh;
	    v2[2] = hd*Math.sin(t2);

	    v3[0] = hd*Math.cos(t2);
	    v3[1] = -hh;
	    v3[2] = hd*Math.sin(t2);

	    // translate local to world
	    multimat(v0, m);
	    multimat(v1, m);
	    multimat(v2, m);
	    multimat(v3, m);

	    // add poligon with world matrix
	    p.add(new Poligon());
	    p.get(p.size()-1).setWv0(v0);
	    p.get(p.size()-1).setWv1(v1);
	    p.get(p.size()-1).setWv2(v2);

	    p.add(new Poligon());    
	    p.get(p.size()-1).setWv0(v2);
	    p.get(p.size()-1).setWv1(v3);
	    p.get(p.size()-1).setWv2(v1);
	}

	// inner side, this won't be draw, just be added as poligons
	for (i=0;i<DIVIDEMODEL;i++) {
	    t1 = i*2*3.1415/DIVIDEMODEL + 3.1415/DIVIDEMODEL;
	    t2 = (i+1)*2*3.1415/DIVIDEMODEL + 3.1415/DIVIDEMODEL;

	    v0[0] = ihd*Math.cos(t1);
	    v0[1] = -hh;
	    v0[2] = ihd*Math.sin(t1);

	    v1[0] = ihd*Math.cos(t1);
	    v1[1] = hh;
	    v1[2] = ihd*Math.sin(t1);

	    v2[0] = ihd*Math.cos(t2);
	    v2[1] = hh;
	    v2[2] = ihd*Math.sin(t2);

	    v3[0] = ihd*Math.cos(t2);
	    v3[1] = -hh;
	    v3[2] = ihd*Math.sin(t2);

	    // translate local to world
	    multimat(v0, m);
	    multimat(v1, m);
	    multimat(v2, m);
	    multimat(v3, m);

	    // add poligon with world matrix
	    p.add(new Poligon());
	    p.get(p.size()-1).setWv0(v0);
	    p.get(p.size()-1).setWv1(v1);
	    p.get(p.size()-1).setWv2(v2);

	    p.add(new Poligon());    
	    p.get(p.size()-1).setWv0(v2);
	    p.get(p.size()-1).setWv1(v3);
	    p.get(p.size()-1).setWv2(v1);
	}


    }

    /*    public void cylinder(double d, double s) {
	gl.glBegin(gl.GL_QUAD_STRIP);
	int i;
	double t;
	for (i=0;i<21;i++) {
	    t = i*2*3.1415/20;
            gl.glNormal3d(Math.cos(t), 0.0, Math.sin(t));
            gl.glVertex3d(d*Math.cos(t), -s, d*Math.sin(t));
            gl.glVertex3d(d*Math.cos(t), s, d*Math.sin(t));
          }
	gl.glEnd();
	}*/

    public void FreeRotate( float n[], float r ){
	float[] v;
	v = new float[16];
	float rrad = (float)Math.toRadians((double)r);

	float w =(float)Math.cos( rrad / 2.0f );
	float w2 = w * w;
	float s = (float)Math.sin( rrad / 2.0f );
	float x = n[0] * s;
	float y = n[1] * s;
	float z = n[2] * s;
	float x2 = x * x;
	float y2 = y * y;
	float z2 = z * z;

	v[0] = w2 + x2 - y2 - z2;
	v[4] = 2 * ( ( x * y ) - ( w * z ) );
	v[8] = 2 * ( ( x * z ) + ( w * y ) );
	v[12] = 0.0f;

	v[1] = 2 * ( ( x * y ) + ( w * z ) );
	v[5] = w2 - x2 + y2 - z2;
	v[9] = 2 * ( ( y * z ) - ( w * x ) );
	v[13] = 0.0f;

	v[2] = 2 * ( ( x * z ) - ( w * y ) );
	v[6] = 2 * ( ( y * z ) + ( w * x ) );
	v[10] = w2 - x2 - y2 + z2;
	v[14] = 0.0f;

	v[3] = 0.0f;
	v[7] = 0.0f;
	v[11] = 0.0f;
	v[15] = 1.0f;

	gl.glMultMatrixf( v,0 );
    }

    public void zoomView() {
	//	double zNear = 2.0d;
	//	double zFar = zNear + diam;
	//	
	//	double left = 0 - diam;
	//	double right = 0 + diam;
	//	double bottom = 0 - diam;
	//	double top = 0 + diam;
	
	//	gl.glMatrixMode(gl.GL_PROJECTION);
	//	gl.glLoadIdentity();
	//	glu.gluLookAt(0.0,0.0,diam, //カメラの座標
	//		      0.0,0.0,0.0, // 注視点の座標
	//		      0.0,1.0,0.0); // 画面の上方向を指すベクトル

	gl.glMatrixMode(gl.GL_MODELVIEW);
	gl.glTranslatef(0.0f, 0.0f, CAMERA+(float)diam);

	//	gl.glMatrixMode(gl.GL_PROJECTION);
	//	gl.glLoadIdentity();
	//	gl.glOrtho(left, right, bottom, top, zNear, zFar);
	//	gl.glMatrixMode(gl.GL_MODELVIEW);
	//	gl.glLoadIdentity();
    }

    private JButton getbutton() {
		if (button == null) {
			button = new JButton();
			//			button.setBounds(new Rectangle(238, 78, 53, 16));
			//			button.setBounds(new Rectangle(0, 0, 100, 16));
			button.setText("add new pipe");
			button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					pipeList.add(new Pipe());
				}
			    });
		}
		return button;
    }

    private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("remove pipe");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.remove(i);
					    break;
					}
				    }
				}
			    });
		}
		return removeButton;
    }

    private JButton getPlusXButton() {
		if (plusXButton == null) {
			plusXButton = new JButton();
			//			plusXButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusXButton.setText("+X");
			plusXButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationX += 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return plusXButton;
    }

    private JButton getMinusXButton() {
		if (minusXButton == null) {
			minusXButton = new JButton();
			//minusXButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusXButton.setText("-X");
			minusXButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationX -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusXButton;
    }

    private JButton getPlusYButton() {
		if (plusYButton == null) {
			plusYButton = new JButton();
			//plusYButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusYButton.setText("+Y");
			plusYButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationY += 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return plusYButton;
    }

    private JButton getMinusYButton() {
		if (minusYButton == null) {
			minusYButton = new JButton();
			//minusYButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusYButton.setText("-Y");
			minusYButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationY -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusYButton;
    }

    private JButton getPlusZButton() {
		if (plusZButton == null) {
			plusZButton = new JButton();
			//plusZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusZButton.setText("+Z");
			plusZButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationZ += 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return plusZButton;
    }

    private JButton getMinusZButton() {
		if (minusZButton == null) {
			minusZButton = new JButton();
			//minusZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusZButton.setText("-Z");
			minusZButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).locationZ -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusZButton;
    }

    private JButton getPlusAngleXButton() {
		if (plusAngleXButton == null) {
			plusAngleXButton = new JButton();
			//plusAngleXButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusAngleXButton.setText("+AngX");
			plusAngleXButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleX += 1;
					    break;
					}
				    }
				}
			    });
		}
		return plusAngleXButton;
    }

    private JButton getMinusAngleXButton() {
		if (minusAngleXButton == null) {
			minusAngleXButton = new JButton();
			//minusAngleXButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusAngleXButton.setText("-AngX");
			minusAngleXButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleX -= 1;
					    break;
					}
				    }
				}
			    });
		}
		return minusAngleXButton;
    }

    private JButton getPlusAngleYButton() {
		if (plusAngleYButton == null) {
			plusAngleYButton = new JButton();
			//plusAngleYButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusAngleYButton.setText("+AngY");
			plusAngleYButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleY += 1;
					    break;
					}
				    }
				}
			    });
		}
		return plusAngleYButton;
    }

    private JButton getMinusAngleYButton() {
		if (minusAngleYButton == null) {
			minusAngleYButton = new JButton();
			//minusAngleYButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusAngleYButton.setText("-AngY");
			minusAngleYButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleY -= 1;
					    break;
					}
				    }
				}
			    });
		}
		return minusAngleYButton;
    }

    private JButton getPlusAngleZButton() {
		if (plusAngleZButton == null) {
			plusAngleZButton = new JButton();
			//plusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusAngleZButton.setText("+AngZ");
			plusAngleZButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleZ += 1;
					    break;
					}
				    }
				}
			    });
		}
		return plusAngleZButton;
    }

    private JButton getMinusAngleZButton() {
		if (minusAngleZButton == null) {
			minusAngleZButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusAngleZButton.setText("-AngZ");
			minusAngleZButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).angleZ -= 1;
					    break;
					}
				    }
				}
			    });
		}
		return minusAngleZButton;
    }

    private JButton getPlusHeightButton() {
		if (plusHeightButton == null) {
			plusHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusHeightButton.setText("+H");
			plusHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sizeh += 0.001;
					    break;
					}
				    }
				}
			    });
		}
		return plusHeightButton;
    }

    private JButton getMinusHeightButton() {
		if (minusHeightButton == null) {
			minusHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusHeightButton.setText("-H");
			minusHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sizeh -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusHeightButton;
    }

    private JButton getPlusDiamButton() {
		if (plusDiamButton == null) {
			plusDiamButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusDiamButton.setText("+D");
			plusDiamButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sized += 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return plusDiamButton;
    }

    private JButton getMinusDiamButton() {
		if (minusDiamButton == null) {
			minusDiamButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusDiamButton.setText("-D");
			minusDiamButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sized -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusDiamButton;
    }

    private JButton getPlusThickButton() {
		if (plusThickButton == null) {
			plusThickButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusThickButton.setText("+T");
			plusThickButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sizet += 0.001;
					    break;
					}
				    }
				}
			    });
		}
		return plusThickButton;
    }

    private JButton getMinusThickButton() {
		if (minusThickButton == null) {
			minusThickButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusThickButton.setText("-T");
			minusThickButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).sizet -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusThickButton;
    }

    private JButton getPlusDensityButton() {
		if (plusDensityButton == null) {
			plusDensityButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusDensityButton.setText("+Dens.");
			plusDensityButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).density += 0.1;
					    break;
					}
				    }
				}
			    });
		}
		return plusDensityButton;
    }

    private JButton getMinusDensityButton() {
		if (minusDensityButton == null) {
			minusDensityButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusDensityButton.setText("-Dens.");
			minusDensityButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).density -= 0.1;
					    break;
					}
				    }
				}
			    });
		}
		return minusDensityButton;
    }

    private JButton getPlusRAButton() {
		if (plusRAButton == null) {
			plusRAButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusRAButton.setText("+RA");
			plusRAButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    for(int j=0; j < pipeList.get(i).rAList.size(); j++){
						pipeList.get(i).rAList.get(j).rag += 0.001;
					    }
					    break;
					}
				    }
				}
			    });
		}
		return plusRAButton;
    }

    private JButton getMinusRAButton() {
		if (minusRAButton == null) {
			minusRAButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusRAButton.setText("-RA");
			minusRAButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    for(int j=0; j < pipeList.get(i).rAList.size(); j++){
						pipeList.get(i).rAList.get(j).rag -= 0.001;
					    }
					    break;
					}
				    }
				}
			    });
		}
		return minusRAButton;
    }

    /*
    private JButton getPlusRAHeightButton() {
		if (plusRAHeightButton == null) {
			plusRAHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusRAHeightButton.setText("+RA Y");
			plusRAHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).raY += 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return plusRAHeightButton;
    }

    private JButton getMinusRAHeightButton() {
		if (minusRAHeightButton == null) {
			minusRAHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusRAHeightButton.setText("-RA Y");
			minusRAHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).raY -= 0.01;
					    break;
					}
				    }
				}
			    });
		}
		return minusRAHeightButton;
    }

    private JButton getToggleRAButton() {
		if (toggleRAButton == null) {
			toggleRAButton = new JButton();
			toggleRAButton.setText("Toggle RA");
			toggleRAButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    for (int i=0; i<pipeList.size(); i++){
					if (pipeList.get(i).isSelected) {
					    pipeList.get(i).hasRA = pipeList.get(i).hasRA ^ true;
					    break;
					}
				    }
				}
			    });
		}
		return toggleRAButton;
    }
    */

    private JButton getPlusDetButton() {
		if (plusDetButton == null) {
			plusDetButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusDetButton.setText("+Dnum");
			plusDetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    detector.num *= 2;
				}
			    });
		}
		return plusDetButton;
    }

    private JButton getMinusDetButton() {
		if (minusDetButton == null) {
			minusDetButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusDetButton.setText("-Dnum");
			minusDetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    if (detector.num > 2) {
					detector.num /= 2;
				    }else {
					detector.num = 2;
				    }
				}
			    });
		}
		return minusDetButton;
    }

    private JButton getPlusDetHeightButton() {
		if (plusDetHeightButton == null) {
			plusDetHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusDetHeightButton.setText("+DH");
			plusDetHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    detector.height += 0.01;
				}
			    });
		}
		return plusDetHeightButton;
    }

    private JButton getMinusDetHeightButton() {
		if (minusDetHeightButton == null) {
			minusDetHeightButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusDetHeightButton.setText("-DH");
			minusDetHeightButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    detector.height -= 0.01;
				}
			    });
		}
		return minusDetHeightButton;
    }

    private JButton getPlusDetDistButton() {
		if (plusDetDistButton == null) {
			plusDetDistButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			plusDetDistButton.setText("+DDist");
			plusDetDistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    detector.distance += 0.01;
				}
			    });
		}
		return plusDetDistButton;
    }

    private JButton getMinusDetDistButton() {
		if (minusDetDistButton == null) {
			minusDetDistButton = new JButton();
			//minusAngleZButton.setBounds(new RoundRectangle2D.Float(0f, 20f, 30f, 20f, 5f, 5f));
			minusDetDistButton.setText("-DDist");
			minusDetDistButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    detector.distance -= 0.01;
				}
			    });
		}
		return minusDetDistButton;
    }

    private JButton getStatButton() {
	if (statButton == null) {
	    statButton = new JButton();
	    statButton.setText("show status");
	    statButton.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent e) {
			fileStatus();
		    }
		});
	}
	return statButton;
    }

    private JButton getCalcButton() {
	if (calcButton == null) {
	    calcButton = new JButton();
	    calcButton.setText("calc the result");
	    calcButton.addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(java.awt.event.ActionEvent e) {
			detector.showCalc(false);
		    }
		});
	}
	return calcButton;
    }

    // Menu Item
    private JMenuItem getMenuOpenFile() {
		if (menuOpenFile == null) {
		    Toolkit tk = Toolkit.getDefaultToolkit();
		    int shortCutKey = tk.getMenuShortcutKeyMask();

			menuOpenFile = new JMenuItem();
			menuOpenFile.setText("Open File...");
			// コマンドのアクセラレータをシステムのデフォルトのキーの組み合わせで登録
			menuOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortCutKey));

			menuOpenFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    // JFileChooser filechooser = new JFileChooser();
				    // int selected = filechooser.showOpenDialog(null);

				    FileDialog fdlg = new FileDialog(frame,"Open", FileDialog.LOAD);
				    fdlg.setDirectory(System.getProperty("user.home")+"/Desktop");
				    try {
					// FileDialogはモーダルダイアログのため
					// JFrameの中にメニューがある場合はメニューも操作できない。
					// しかし、OSXのスクリーンメニューは操作可能であるため明示的にディセーブルしておく.
					bar.setEnabled(false);
					fdlg.setVisible(true);
				    } finally {
					bar.setEnabled(true);
				    }
				    
				    String fname = fdlg.getDirectory()+fdlg.getFile();
				    
				    if (fname == null) {
					// cancel
					return;
				    }else {
					loadModel(fname);
				    }
				}
			    });
		}
		return menuOpenFile;
    }

    private void loadModel(String fname){
	JFrame frame = new JFrame();

	try{
	    String charSet  = "utf-8";
	    BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(new File(fname)),charSet));// 省略するとシステム標準
	    String line;
	    int pipeno;
	    Pipe addedPipe;
	    RA addedRA;

	    pipeList.clear();

	    while( (line=br.readLine())!=null ){
		String[] value;
		if (line.substring(0, 7).equals("***Pipe")) {
		    pipeList.add(new Pipe());
		    addedPipe = pipeList.get(pipeList.size()-1);

		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.angleX = Float.parseFloat(value[1].trim());
		    addedPipe.angleY = Float.parseFloat(value[2].trim());
		    addedPipe.angleZ = Float.parseFloat(value[3].trim());
		    
		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.locationX = Float.parseFloat(value[1].trim());
		    addedPipe.locationY = Float.parseFloat(value[2].trim());
		    addedPipe.locationZ = Float.parseFloat(value[3].trim());

		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.sizet = Float.parseFloat(value[1].trim());
		    addedPipe.sized = Float.parseFloat(value[2].trim());
		    addedPipe.sizeh = Float.parseFloat(value[3].trim());

		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.density = Float.parseFloat(value[1].trim());

		    /*
		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.hasRA = value[1].trim().equals("1");

		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.raY = Float.parseFloat(value[1].trim());

		    line=br.readLine();
		    value = line.split(",");
		    addedPipe.rag = Float.parseFloat(value[1].trim());
		    */
		}else if (line.substring(0, 4).equals("**RA")) {
		    addedPipe = pipeList.get(pipeList.size()-1);
		    addedPipe.addRA(0,0,0,false);
		    addedRA = addedPipe.rAList.get(addedPipe.rAList.size()-1);

		    line=br.readLine();
		    value = line.split(",");
		    addedRA.isInside = value[1].trim().equals("1");

		    line=br.readLine();
		    value = line.split(",");
		    addedRA.raX = Float.parseFloat(value[1].trim());
		    addedRA.raY = Float.parseFloat(value[2].trim());
		    addedRA.raZ = Float.parseFloat(value[3].trim());
		    
		    line=br.readLine();
		    value = line.split(",");
		    addedRA.rag = Float.parseFloat(value[1].trim());
		}else if (line.equals("***Detector***")) {
		    line=br.readLine();
		    value = line.split(",");
		    detector.num = Integer.parseInt(value[1].trim());
		    
		    line=br.readLine();
		    value = line.split(",");
		    detector.height = Float.parseFloat(value[1].trim());
		    detector.distance = Float.parseFloat(value[2].trim());
		}
	    }
	    br.close();
	}
	catch(Exception e){
	    e.printStackTrace(System.err);
	    JOptionPane.showMessageDialog(frame, "Cannot Open file. "+e);	    
      }
    }


    public void multimat(double v[], double m[]) {
	double[] ret = new double[3];

	//	System.out.printf("%f, %f, %f, %f\n", m[0], m[4], m[8], m[12]);
	//	System.out.printf("%f, %f, %f, %f\n", m[1], m[5], m[9], m[13]);
	//	System.out.printf("%f, %f, %f, %f\n", m[2], m[6], m[10], m[14]);
	//	System.out.printf("%f, %f, %f, %f\n\n", m[3], m[7], m[11], m[15]);

	//		ret[0] = v[0]*m[0] + v[1]*m[1] + v[2]*m[2] + m[3];
	//		ret[1] = v[0]*m[4] + v[1]*m[5] + v[2]*m[6] + m[7];
	//		ret[2] = v[0]*m[8] + v[1]*m[9] + v[2]*m[10] + m[11];

	ret[0] = v[0]*m[0] + v[1]*m[4] + v[2]*m[8] + m[12];
	ret[1] = v[0]*m[1] + v[1]*m[5] + v[2]*m[9] + m[13];
	ret[2] = v[0]*m[2] + v[1]*m[6] + v[2]*m[10] + m[14];

	v[0] = ret[0];
	v[1] = ret[1];
	v[2] = ret[2];
    }

    public void normalize(double v[], double x, double y, double z) {
	double size = Math.sqrt(x*x+y*y+z*z);

	v[0] = x/size;
	v[1] = y/size;
	v[2] = z/size;
    }

    public void showStatus() {
	for (int i=0; i<pipeList.size(); i++){	
	    System.out.printf("***Pipe: %d***\n", i);
	    pipeList.get(i).showStatus();	    
	}
	System.out.printf("***Detector***\n");	
	detector.showStatus();
    }

    public void fileStatus() {
	Date date = new Date();
	String home = System.getProperty("user.home");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssyyyy");
	String datef = sdf.format(date);  
	String filename = home + "/Desktop/result" + datef + ".csv";

	try {
	    FileOutputStream outfile = new FileOutputStream(filename);
	    OutputStreamWriter out = new OutputStreamWriter(outfile, "Shift_JIS");

	    for (int i=0; i<pipeList.size(); i++){	
		out.write(String.format("***Pipe: %d***\n", i));
		out.write(pipeList.get(i).csvStatus());
	    }
	    out.write(String.format("***Detector***\n"));	
	    out.write(detector.csvStatus());
	    out.write(String.format("***Result***\n"));	      
	    out.write(detector.calc(false));

	    out.close();

	} catch(IOException e) {
	}
    }

    public void displayChanged(GLAutoDrawable drawable,
                               boolean modeChanged,
                               boolean deviceChanged) {}
 
    public void dispose(GLAutoDrawable drawable) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public static void main(String[] args) {
	System.setProperty("apple.laf.useScreenMenuBar", "true");
        new Wirecylinder();
    }


    private class Pipe {
	public float angleX = 0.0f;
	public float angleY = 0.0f;
	public float angleZ = 0.0f;
	public float locationX = 0.0f;
	public float locationY = 0.0f;
	public float locationZ = 0.0f;
	public String name = "pipe";

	public float sizet = 0.0045f;
	public float sized = 0.1143f;
	public float sizeh = 0.8f;

	public float density = 7.874f; // iron

	public ArrayList<Poligon> poligonList;

	public boolean isSelected = false;

	// tenchi gyouretsu
	public double[] m = new double[16];

	// about RA
	public ArrayList<RA> rAList;
	//	public boolean hasRA = false;
	//	public float raX = 0.0f;
	//	public float raY = 0.0f;
	//	public float raZ = 0.0f;
	//	public float rag = 1.0f;
	//	public double wraX = 0.0;
	//	public double wraY = 0.0;
	//	public double wraZ = 0.0;

	public Pipe() {
	   poligonList = new ArrayList<Poligon>();
	   rAList = new ArrayList<RA>();
	}

	public void init(){
	}

	public void display() {
	    // draw pipe
	    gl.glPushMatrix();
	    gl.glTranslatef(locationX,locationY,locationZ);
	    // マウスの移動量に応じて移動
	    //	    gl.glTranslatef(distanceX, distanceY, 0.0f);

	    //	    float[] xx = {1.0f, 0, 0};
	    //	    float[] yy = {0, 1.0f, 0};
	    //	    float[] zz = {0, 0, 1.0f};
	    //	    FreeRotate(xx, angleX);
	    //	    FreeRotate(yy, angleY);
	    //	    FreeRotate(zz, angleZ);
	    gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
	    gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);
	    gl.glRotatef(angleZ, 0.0f, 0.0f, 1.0f);

	    //draw pipe
	    if (isSelected) {
		gl.glColor4f(1.0f,0,0,0);
	    }else{
		gl.glColor4f(1.0f,0.71f,0.76f,0);
	    }

	    wirecylinder(sized, sizeh, sizet, poligonList);

	    //draw RA
	    radisplay();

	    gl.glPopMatrix();
	}

	private void addRA(float x, float y, float z, boolean isInside){
	    int n;

	    rAList.add(new RA());
	    n = rAList.size();
	    rAList.get(n-1).raX = x;
	    rAList.get(n-1).raY = y;
	    rAList.get(n-1).raZ = z;
	    rAList.get(n-1).isInside = isInside;
	}

	private void addRALines(int lines, float rag, float fd, float fh, boolean isInside){
	    double t1;
	    float hh, hd;
	    hh = fh/2; // half height = (full height)/2
	    hd = fd/2; // half diameter = (full diameter)/2

	    for (int i=0; i < lines; i++) {
		t1 = i*2*3.1415/lines; // kakudo (radian)
		addRA((float)(hd*Math.cos(t1)), 0.0f, (float)(hd*Math.sin(t1)), isInside); // for the center
		for (int j=1; j < 5; j++){ // the others. (4x2) total 9 RAs
		    addRA((float)(hd*Math.cos(t1)), hh*j/4, (float)(hd*Math.sin(t1)), isInside);
		    addRA((float)(hd*Math.cos(t1)), -hh*j/4, (float)(hd*Math.sin(t1)), isInside);
		}
	    }
	}

	private void radisplay(){
	    for (int i=0; i < rAList.size(); i++){
		rAList.get(i).display();
	    }
	}

	public void showStatus(){
	    RA currentRA;

	    System.out.printf("Angle XYZ: %f, %f, %f\n", angleX, angleY, angleZ);
	    System.out.printf("Location XYZ: %f, %f, %f\n", locationX, locationY, locationZ);
	    System.out.printf("Size TDH: %f, %f, %f\n", sizet, sized, sizeh);
	    System.out.printf("Density %f\n", density);

	    for (int i=0; i < rAList.size(); i++){
		System.out.printf("**RA: %d**\n", i);		
		currentRA = rAList.get(i);
		if (currentRA.isInside) {
		    System.out.printf("isInside: Yes.\n");
		}else{
		    System.out.printf("isInside: No.\n");
		}
		System.out.printf("Location XYZ: %f, %f, %f\n", currentRA.raX, currentRA.raY, currentRA.raZ);
		System.out.printf("Uranium: %fg\n", currentRA.rag);
	    }
	}

	public String getStatus(){
	    RA currentRA;
	    String result;
	    result = String.format("Angle XYZ: %f, %f, %f<br>", angleX, angleY, angleZ);
	    result += String.format("Location XYZ: %f, %f, %f<br>", locationX, locationY, locationZ);
	    result += String.format("Size TDH: %f, %f, %f<br>", sizet, sized, sizeh);
	    result += String.format("Density %f<br>", density);

	    for (int i=0; i < rAList.size(); i++){
		result += String.format("**RA: %d**<br>", i);		
		currentRA = rAList.get(i);
		if (currentRA.isInside) {
		    result += String.format("isInside, 1<br>");
		}else{
		    result += String.format("isInside, 0<br>");
		}
		result += String.format("Location XYZ: %f, %f, %f<br>", currentRA.raX, currentRA.raY, currentRA.raZ);
		result += String.format("Uranium: %fg<br>", currentRA.rag);
	    }
	    return result;
	}

	public String csvStatus(){
	    RA currentRA;
	    String result;
	    result = String.format("Angle XYZ, %f, %f, %f\n", angleX, angleY, angleZ);
	    result += String.format("Location XYZ, %f, %f, %f\n", locationX, locationY, locationZ);
	    result += String.format("Size TDH, %f, %f, %f\n", sizet, sized, sizeh);
	    result += String.format("Density, %f\n", density);

	    for (int i=0; i < rAList.size(); i++){
		result += String.format("**RA: %d**\n", i);		
		currentRA = rAList.get(i);
		if (currentRA.isInside) {
		    result += String.format("isInside, 1\n");
		}else{
		    result += String.format("isInside, 0\n");
		}
		result += String.format("Location XYZ, %f, %f, %f\n", currentRA.raX, currentRA.raY, currentRA.raZ);
		result += String.format("Uranium, %f\n", currentRA.rag);
	    }
	    return result;
	}
    }

    private class RA {
	public float raX = 0.0f;
	public float raY = 0.0f;
	public float raZ = 0.0f;
	public float rag = 1.0f;

	public boolean isSelected = false;
	public boolean isInside = false;

	public double wraX = 0.0;
	public double wraY = 0.0;
	public double wraZ = 0.0;

	public void display(){
	    double[] v = {0,0,0};
	    gl.glPushMatrix();
	    gl.glColor4f(0,1.0f,0,0); // the color is green
	    if (isInside) { // inside of pipe
		gl.glTranslatef(raX-0.0000005f,raY, raZ);
	    }else{ // outside of pipe
		gl.glTranslatef(raX+0.0000005f,raY, raZ);
	    } 
	    gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, m, 0);
	    multimat(v,m);
	    wraX = v[0];
	    wraY = v[1];
	    wraZ = v[2];
	    glut.glutWireSphere(0.005d, 36, 18);
	    gl.glPopMatrix();		
	}
    }


    private class Detector {
	public int num = 12;
	public double height = 0.0;
	public double distance = 0.75d - 0.5692d; // 75cm from the center of the drum can
	public double[] wX = new double[360];
	public double[] wY = new double[360];
	public double[] wZ = new double[360];

	public void display() {
	    double[] v = {0,0,0};
	    double[] m = new double[16];

	    double t;
	    double x, y, z;
	    gl.glColor4f(0,0,1.0f,0);
	    // start drawing detector
	    for (int i=0;i<num;i++) {
		t = i*2*3.1415/num;
		// init v 
		Arrays.fill(v, 0);
		// set the place to draw
		gl.glPushMatrix();
		x = (drumdiam/2.0d+distance)*Math.cos(t);
		y = height;
		z = (drumdiam/2.0d+distance)*Math.sin(t);
		gl.glTranslated(x,y,z);
		// get the world zahyo
		gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, m, 0);
		multimat(v,m);
		wX[i] = v[0];
		wY[i] = v[1];
		wZ[i] = v[2];
		// draw detector as wiresphere
		glut.glutWireSphere(0.03d, 36, 18);
		gl.glPopMatrix();
	    }
	}

	public void showStatus(){
	    System.out.printf("Detector Number: %d\n", num);
	    System.out.printf("Location HD: %f, %f\n", height, distance);
	}

	public String csvStatus(){
	    String result;
	    result = String.format("Detector Number, %d\n", num);
	    result += String.format("Location HD, %f, %f\n", height, distance);
	    return result;
	}

	public void showCalc(Boolean debug) {
	    String result;
	    result = calc(debug);
	    System.out.printf(result);
	}

	public String calc(Boolean debug) {
	    double[] orig = new double[3];
	    double[] dir = new double[3];
	    double[] retTUV = new double[3];
	    int[] pnum = new int[16];
	    double[] pdist = new double[16];
	    double radetdist = 0;
	    double density = 0;
	    double rag = 0;
	    double n1001sum = 0;
	    double n766sum = 0;
	    double mud1001sum = 0;
	    double mud766sum = 0;
	    double s1001g = 0;
	    double s766g = 0;
	    double distance = 0;
	    String result;

	    ArrayList<Integer> thickList;
	    result = "";

	    //System.out.printf("****** start detecting ******\n");
	    for (int i=0; i<num; i++){ // select a detector
		n1001sum = 0;
		n766sum = 0;
		for (int j=0; j<pipeList.size(); j++){ // select a Pipe
		    mud1001sum = 0;
		    mud766sum = 0;
		    for (int jj=0; jj < pipeList.get(j).rAList.size(); jj++){ // select a RA
			if (debug) System.out.println("calc!");
			RA currentRA;

			currentRA = pipeList.get(j).rAList.get(jj);
			rag = currentRA.rag; // gram of the RA

			s1001g = S1001 * rag;
			s766g = S766 * rag;

			orig[0] = currentRA.wraX;
			orig[1] = currentRA.wraY;
			orig[2] = currentRA.wraZ;

			//distance between RA and detector
			radetdist = Math.sqrt(Math.pow(wX[i]-orig[0],2)+Math.pow(wY[i]-orig[1],2)+Math.pow(wZ[i]-orig[2],2));
			// to hold the remaining distance
			distance = radetdist; 
			normalize(dir, wX[i]-orig[0], wY[i]-orig[1], wZ[i]-orig[2]);
			
			// for drum can as shield
			density = drumdensity; 

			if (debug) {
			    System.out.printf("detector[%d], rapipe[%d], drumcan:\n", i, j);
			    System.out.printf("dir[0]:%f, dir[1]:%f, dir[2]:%f\n", dir[0], dir[1], dir[2]);
			}
			int n = 0;
			for (int m=0; m<drumPoligonList.size(); m++){
			    if (drumPoligonList.get(m).triangleIntersect(orig, dir, retTUV)){
				pnum[n] = m;
				pdist[n] = retTUV[0];
				n++;
			    }
			}
			sort(pnum, pdist, n);
			if (debug) {
			    for (int m=0; m<n; m++){
				if (m==0) {
				    System.out.printf("poligon[%d], dist: %f\n", pnum[0], pdist[0]);
				}else{
				    System.out.printf("poligon[%d], dist: %f, diff: %f\n", pnum[m], pdist[m], pdist[m]-pdist[m-1]);
				}
			    }
			    System.out.printf("\n");
			}
			for (int m=1; m<n; m++){
			    if ((pdist[m]-pdist[m-1])>1e-10) { //if the distance is almost zero, ignore it
				mud1001sum += MURHO1001*density*(pdist[m]-pdist[m-1])*100;
				mud766sum += MURHO766*density*(pdist[m]-pdist[m-1])*100;
				distance -= (pdist[m]-pdist[m-1]);
			    }
			}
			
			// for pipes as shield
			for (int k=0; k<pipeList.size(); k++){
			    density = pipeList.get(k).density; // set the density of the pipe
			    if (debug){
				System.out.printf("detector[%d], rapipe[%d], pipe[%d]:\n", i, j, k);
				System.out.printf("dir[0]:%f, dir[1]:%f, dir[2]:%f\n", dir[0], dir[1], dir[2]);
			    }
			    n = 0; // to use for counting poligons with collision
			    for (int m=0; m<pipeList.get(k).poligonList.size(); m++){
				//System.out.printf("poligon[%d] ", m);
				if (pipeList.get(k).poligonList.get(m).triangleIntersect(orig, dir, retTUV)){
				    pnum[n] = m;
				    pdist[n] = retTUV[0];
				    n++;
				    //System.out.printf("%dth, poligon[%d], find: %f\n", n, m, retTUV[0]);
				}
			    }
			    sort(pnum, pdist, n);
			    if (debug) {
				for (int m=0; m<n; m++){
				    if (m==0) {
					System.out.printf("poligon[%d], dist: %f\n", pnum[0], pdist[0]);
				    }else{
					System.out.printf("poligon[%d], dist: %f, diff: %5.10f\n", pnum[m], pdist[m], pdist[m]-pdist[m-1]);
				    }
				}
			    System.out.printf("\n");
			    }
			    for (int m=1; m<n; m++){
				Boolean flag = false;
				if ((pdist[m]-pdist[m-1])>1e-10) { //if the distance is almost zero, ignore it
				    if (flag) {
					flag = false;
				    } else {
					flag = true;
					mud1001sum += MURHO1001*density*(pdist[m]-pdist[m-1])*100;
					mud766sum += MURHO766*density*(pdist[m]-pdist[m-1])*100;
					distance -= (pdist[m]-pdist[m-1]);
				    }
				}
			    }
			}
			mud1001sum += MURHO1001*AIRDENSITY*distance*100;
			mud766sum += MURHO766*AIRDENSITY*distance*100;
			
			n1001sum += s1001g * Math.exp(-1 * mud1001sum)/(4*Math.PI*Math.pow(radetdist*100,2));
			n766sum += s766g * Math.exp(-1 * mud766sum)/(4*Math.PI*Math.pow(radetdist*100,2));
		    }
		}
		//System.out.printf("detector[%d], 1001keV: %3.10f\n", i, n1001sum);
		//System.out.printf("detector[%d], 766keV: %3.10f\n", i, n766sum);
		//System.out.printf("\n");
		result += String.format("detector[%d], 1001keV, %3.10f\n", i, n1001sum);
		result += String.format("detector[%d], 766keV, %3.10f\n", i, n766sum);
	    }
	    return result;
	}

	private void sort(int num[], double dist[], int n) {
	    for (int i=0; i<n-1; i++){
		for (int j=n-1; j>i; j--){
		    if (dist[j]<dist[j-1]) {
			int t = num[j];
			double td = dist[j];
			num[j] = num[j-1];
			dist[j] = dist[j-1];
			num[j-1] = t;
			dist[j-1] = td;
		    }
		}
	    }
	}

    }

    private class Poligon {
	public double[] wv0 = new double[3];
	public double[] wv1 = new double[3];
	public double[] wv2 = new double[3];

	public void init(){
	}
	
	public void setWv0(double v[]) {
	    wv0[0] = v[0];
	    wv0[1] = v[1];
	    wv0[2] = v[2];
	}

	public void setWv1(double v[]) {
	    wv1[0] = v[0];
	    wv1[1] = v[1];
	    wv1[2] = v[2];
	}

	public void setWv2(double v[]) {
	    wv2[0] = v[0];
	    wv2[1] = v[1];
	    wv2[2] = v[2];
	}

	public void vec3Sub(double c[], double a[], double b[]) { //   ... c = a - b を計算  
	    c[0] = a[0] - b[0];
	    c[1] = a[1] - b[1];
	    c[2] = a[2] - b[2];
	}

	public double vec3Dot(double a[], double b[]) { // ... aとbの内積を求め、結果はe（double）
	    double e = 0.0d;
	    e = a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
	    return e;
	}
	
	public void vec3Cross(double c[], double a[], double b[]) { // ... aとbの外積を求め、結果はc
	    c[0] = a[1]*b[2]-a[2]*b[1];
	    c[1] = a[2]*b[0]-a[0]*b[2];
	    c[2] = a[0]*b[1]-a[1]*b[0];
	}

	public Boolean triangleIntersect(double Orig[], double dir[], double retTUV[]) {
	    //also uses wv0 wv1 and wv2
	    double[] e1 = new double[3];
	    double[] e2 = new double[3];
	    double[] pvec = new double[3];
	    double[] tvec = new double[3];
	    double[] qvec = new double[3];
	    double det;
	    double t, u, v;
	    double inv_det;

	    vec3Sub(e1, wv1, wv0);
	    vec3Sub(e2, wv2, wv0);

	    vec3Cross(pvec, dir, e2);
	    det = vec3Dot(e1, pvec);

	    //	    System.out.printf("det:%f ", det);

	    if (det > (1e-12)) {
		vec3Sub(tvec, Orig, wv0);
		u = vec3Dot(tvec, pvec);
		//		System.out.printf("u:%10f ", u);
		if (u < 0.0 || u > det) return false;

		vec3Cross(qvec, tvec, e1);

		v = vec3Dot(dir, qvec);
		//		System.out.printf("v:%10f\n", v);
		if (v < 0.0 || u + v > det) return false;

	    } else if (det < -(1e-12)) {
		vec3Sub(tvec, Orig, wv0);

		u = vec3Dot(tvec, pvec);
		if (u > 0.0 || u < det) return false;

		vec3Cross(qvec, tvec, e1);

		v = vec3Dot(dir, qvec);
		if (v > 0.0 || u + v < det) return false;
		
	    } else {
		return false;
	    }

	    inv_det = 1.0 / det;

	    t = vec3Dot(e2, qvec);
	    t *= inv_det;
	    u *= inv_det;
	    v *= inv_det;

	    if (t < 0) return false;

	    retTUV[0] = t;
	    retTUV[1] = u;
	    retTUV[2] = v;

	    return true;    //hit!!
	}
    }
}

