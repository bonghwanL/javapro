package project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LibraryLogin extends JFrame implements ActionListener {
	JButton btnMe, btnAd, btnExit;

	JLabel lblId, lblPw;
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	public LibraryLogin() { // 생성자 - 버튼 및 레이아웃 생성
		super("로그인");

		layInit();

		setSize(450, 400);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 화면 중앙에 위치하도록 설정
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int re = JOptionPane.showConfirmDialog(LibraryLogin.this, "정말종료하시겠습니까?", "종료",
						JOptionPane.OK_CANCEL_OPTION);
				if (re == JOptionPane.OK_OPTION) {
					System.exit(0);

				} else {
					setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				}
			}
		});

	}

	private void layInit() { // 버튼 레이아웃 설정
		setLayout(new GridLayout(3, 0));

		JPanel pn1 = new JPanel();
		JPanel pn2 = new JPanel();
		JPanel pn3 = new JPanel();

		btnAd = new JButton("관리자");
		btnExit = new JButton("종료");
		btnMe = new JButton("회원");
		pn1.add(btnMe);
		pn1.add(btnAd);
		pn1.add(btnExit);
		add("2,2", pn2);
		add("2,1", pn1);
		add("2,3", pn3);

		btnAd.setPreferredSize(new Dimension(100, 75));
		btnAd.setBackground(new Color(0, 150, 200));
		btnAd.addActionListener(this);
		btnExit.setBackground(new Color(175, 0, 0));
		btnMe.setBackground(Color.CYAN);
		btnExit.addActionListener(this);

		btnExit.setPreferredSize(new Dimension(100, 75));
		btnMe.setPreferredSize(new Dimension(100, 75));
		btnMe.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) { // 버튼 클릭시 각 동작 수행. 회원일시 회원 로그인 창 관리자일경우 관리자 로그인창
		try {

			if (e.getSource() == btnAd) {
				ClAdmin cladmin = new ClAdmin(LibraryLogin.this);
				
			} else if (e.getSource() == btnMe) {
				ClMember clmem = new ClMember(LibraryLogin.this);
				
			} else if (e.getSource() == btnExit) {
				int re = JOptionPane.showConfirmDialog(this, "종료를 원하십니까?", "Quit", JOptionPane.OK_CANCEL_OPTION);
				if (re == JOptionPane.OK_OPTION) {
					JOptionPane.showMessageDialog(this, "돌아오세요~");
					System.exit(0);

				} else {
					setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				}
			}

		} catch (Exception e2) {
			System.out.println("액션 오류 ;" + e);

		}

	}
	
	//회원 버튼 클릭시 동작 수행 
	
	class ClMember extends JDialog implements ActionListener {
		JTextField txtNo, txtName;
		JLabel lblNo, lblName;
		JButton btnLogMe, btnAddMe;
		Connection conn;
		PreparedStatement pstmt;
		ResultSet rs;

		public ClMember(Frame frame) {
			super(frame, "회원 로그인");
			setLayout(new GridLayout(3, 2));

			JPanel pn1 = new JPanel();
			JPanel pn2 = new JPanel();
			JPanel pn3 = new JPanel();
			JPanel pn4 = new JPanel();
			JPanel pn5 = new JPanel();
			JPanel pn6 = new JPanel();
			// 회원ID
			lblNo = new JLabel("회원 번호 : ");
			pn1.add(lblNo);
			txtNo = new JTextField("", 5);
			pn1.add(txtNo);
			lblName = new JLabel("회원 이름 : ");
			pn2.add(lblName);
			txtName = new JTextField("", 5);
			pn2.add(txtName);
			// 로그인 버튼
			btnLogMe = new JButton("로그인");
			pn3.add(btnLogMe);
			btnLogMe.addActionListener(this);
			// 관리자 추가 버튼
			btnAddMe = new JButton("회원 신규등록");
			pn3.add(btnAddMe);

			add(pn1);
			add(pn2);
			add(pn3);
			setSize(350, 300);
			setLocationRelativeTo(null);
			setVisible(true);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}

			});
		}

		private void accDb() {
			try {
				Class.forName("org.mariadb.jdbc.Driver");

			} catch (Exception e) {
				System.out.println("accDB err : " + e);
			}
		}
		
		private void memLogin() {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
				String msql = "select*from userinfo where uno =? and uname =?";
				pstmt = conn.prepareStatement(msql);
				pstmt.setString(1, txtNo.getText());
				pstmt.setString(2, txtName.getText());

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dispose();
					JOptionPane.showMessageDialog(this, "회원 로그인 성공");
					String uno = rs.getString("uno");
					LibraryMe libraryme = new LibraryMe();

				} else {
					JOptionPane.showMessageDialog(this, "아이디나 비밀번호가 틀립니다.");
				}

			} catch (Exception e) {
				System.out.println("adLogin err : " + e);
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception e3) {
					System.out.println("finally close err : " + e3);
				}

			}
		}	
		
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnLogMe) {
			if(e.getSource().equals("")) {
				JOptionPane.showMessageDialog(this, "회원번호를 입력해주세요");
				txtNo.requestFocus();
				return;
			}else {
				memLogin();
				
			}
		}
		
	}
	}

	// 관리자 버튼 클릭시 동작 수행하는 클래스
	class ClAdmin extends JDialog implements ActionListener {
		JTextField txtId;
		JPasswordField txtPw;
		JLabel lblId, lblPw;
		JButton btnLogAd, btnAddAd, btnClose;
		Connection conn;
		PreparedStatement pstmt;
		ResultSet rs;

		public ClAdmin(Frame frame) {
			super(frame, "관리자 로그인");
			setLayout(new GridLayout(3, 2));

			JPanel pn1 = new JPanel();
			JPanel pn2 = new JPanel();
			JPanel pn3 = new JPanel();
			JPanel pn4 = new JPanel();
			JPanel pn5 = new JPanel();
			JPanel pn6 = new JPanel();
			// 관리자 ID
			lblId = new JLabel("관리자 ID : ");
			pn1.add(lblId);
			txtId = new JTextField("", 5);
			pn1.add(txtId);
			lblPw = new JLabel("관리자 PW : ");
			pn2.add(lblPw);
			txtPw = new JPasswordField("", 5);
			pn2.add(txtPw);
			// 로그인 버튼
			btnLogAd = new JButton("로그인");
			pn3.add(btnLogAd);
			btnLogAd.addActionListener(this);
			// 관리자 추가 버튼
			btnAddAd = new JButton("관리자 추가");
			pn3.add(btnAddAd);
			btnAddAd.addActionListener(this);

			add(pn1);
			add(pn2);
			add(pn3);
			setSize(350, 300);
			setLocationRelativeTo(null);
			setVisible(true);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}

			});
		}

		private void accDb() {
			try {
				Class.forName("org.mariadb.jdbc.Driver");

			} catch (Exception e) {
				System.out.println("accDB err : " + e);
			}

		}

		private void adLogin() {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
				String sql = "select*from admininfo where adminid =? and adminpw =?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, txtId.getText());
				pstmt.setString(2, txtPw.getText());

				rs = pstmt.executeQuery();

				if (rs.next()) {
					dispose();
					JOptionPane.showMessageDialog(this, "관리자 로그인 성공");
					String adminid = rs.getString("adminid");
					Library library = new Library();

				} else {
					JOptionPane.showMessageDialog(this, "아이디나 비밀번호가 틀립니다.");
				}

			} catch (Exception e) {
				System.out.println("adLogin err : " + e);
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception e3) {
					System.out.println("finally close err : " + e3);
				}

			}
		}

		class Addad extends JDialog implements ActionListener {
			JTextField txtIdadd;
			JPasswordField txtPwadd;
			JLabel lblIdadd, lblPwadd, lblShow, lblNo;
			JButton btnAddAd, btnClose;
			Connection conn;
			PreparedStatement pstmt;
			ResultSet rs;
			
			public Addad() {
				setTitle("관리자 추가");
				layOutadd();
				setVisible(true);
				setSize(300, 300);
				setLocationRelativeTo(null);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			}
			

			public void layOutadd() {
				setLayout(new GridLayout(4, 2));

				JPanel pn1 = new JPanel();
				JPanel pn2 = new JPanel();
				JPanel pn3 = new JPanel();
				JPanel pn4 = new JPanel();
				JPanel pn5 = new JPanel();
				JPanel pn6 = new JPanel();
				JPanel pn7 = new JPanel();
				JPanel pn8 = new JPanel();
			
				
				lblIdadd = new JLabel("관리자 ID");
				lblPwadd = new JLabel("관리자 PW");
				lblNo = new JLabel("관리자 번호는 : ");
				lblShow = new JLabel("");
				
				txtIdadd = new JTextField("", 5);
				txtPwadd = new JPasswordField("", 5);
				
				btnAddAd = new JButton("관리자 추가");
				btnAddAd.addActionListener(this);
				btnClose = new JButton("취소");
				btnClose.addActionListener(this);
				
				pn1.add(lblIdadd);
				pn1.add(txtIdadd);
				pn3.add(lblPwadd);
				pn3.add(txtPwadd);
				pn5.add(lblNo);
				pn5.add(lblShow);
				pn7.add(btnAddAd);
				pn8.add(btnClose);
				
				add(pn1);
				add(pn2);
				add(pn3);
				add(pn4);
				add(pn5);
				add(pn6);
				add(pn7);
				add(pn8);
			}
			
			public void addAd() {
				try {
					int new_adminno = 0;
					String sql = "select max(adminno) from admininfo";
					pstmt = conn.prepareStatement(sql);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						new_adminno = rs.getInt(1);
					}
					try {
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
						String asql = "insert into admininfo values(?,?,?,?)";
						pstmt = conn.prepareStatement(asql);
						pstmt.setInt(1, new_adminno + 1);
						pstmt.setString(2, txtIdadd.getText());
						pstmt.setString(3, txtPwadd.getText());
				//		pstmt.setString(4, txt.getText());
					} catch (Exception e) {
						// TODO: handle exception
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		}
	
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnLogAd) {
					adLogin();

					System.out.println("출력");

				} else if (e.getSource() == btnAddAd) {
					
//					String delNo = JOptionPane.showInputDialog(this, "새로운 관리자 ID 입력");
//					if(delNo == null) return;
					Addad addad = new Addad();
				}

			} catch (Exception e2) {
				// TODO: handle exception
			}

		}
	}

	public static void main(String[] args) {
		new LibraryLogin();

	}
}
