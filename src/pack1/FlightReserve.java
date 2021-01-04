package pack1;	//찾기 (!로그인, !예약, !데이터관리)

import java.awt.CardLayout;
import java.awt.Color;
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
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class FlightReserve {	//[수정? ] 구매날짜, 나이

	private Connection conn;
	private PreparedStatement prep;
	private ResultSet rs;

	public FlightReserve() {
		new LoginMain();

	}
	
	// 로그인 class (!로그인)
	// 회원/비회원 전용 (예약하기, 구매내역확인하기, 회원가입) & 직원 전용 (데이터 관리)
	public class LoginMain extends JFrame implements ActionListener {
		JButton btnCstm, btnAdmin, btnNonJoin, btnJoin, btnlogin;
		JLabel lblCsNo, lblCsPw, lblAdminId, lblAdminPw;
		JTextField txtCsId, txtCsPw, txtAdminId, txtAdminPw;
		CardLayout card = new CardLayout();
		JPanel pn2 = new JPanel();

		public LoginMain() {
			super("로그인");

			loginlayout();
			accDb();

			setBounds(300, 300, 400, 150);
			setVisible(true);

			addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					int re = JOptionPane.showConfirmDialog(LoginMain.this, "정말 종료할까요?", "종료",
							JOptionPane.OK_CANCEL_OPTION);
					
					//종료 전 DB 접속 깔끔히 종료
					if (re == JOptionPane.OK_OPTION) {
						try {
							if (rs != null)
								rs.close();
							if (prep != null)
								prep.close();
							if (conn != null)
								conn.close();
						} catch (Exception e2) {

						}
						setDefaultCloseOperation(EXIT_ON_CLOSE);
						// System.exit(0);
					} else {
						setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					}
				}
			});
		}

		private void loginlayout() {

			// 1행 (구분)
			btnCstm = new JButton("회원/비회원");
			btnAdmin = new JButton("직원");
			btnCstm.addActionListener(this);
			btnAdmin.addActionListener(this);

			JPanel pn1 = new JPanel();
			pn1.add(btnCstm);
			pn1.add(btnAdmin);
			add("North", pn1);

			// 2행 (값 입력 창)
			// 2-1행 (회원/비회원)
			JPanel pn2_1 = new JPanel();
			lblCsNo = new JLabel("회원번호");
			lblCsPw = new JLabel("비밀번호");
			txtCsId = new JTextField(10);
			txtCsPw = new JTextField(10);
			pn2_1.add(lblCsNo);
			pn2_1.add(txtCsId);
			pn2_1.add(lblCsPw);
			pn2_1.add(txtCsPw);

			// 2-2행 (관리자)
			JPanel pn2_2 = new JPanel();
			lblAdminId = new JLabel("관리자 ID");
			lblAdminPw = new JLabel("Password");
			txtAdminId = new JTextField(10);
			txtAdminId.setBackground(Color.LIGHT_GRAY);
			txtAdminPw = new JTextField(10);
			txtAdminPw.setBackground(Color.LIGHT_GRAY);
			pn2_2.add(lblAdminId);
			pn2_2.add(txtAdminId);
			pn2_2.add(lblAdminPw);
			pn2_2.add(txtAdminPw);

			pn2.setLayout(card);
			pn2.add("Cs", pn2_1);
			pn2.add("Ad", pn2_2);
			add("Center", pn2);

			// 3행 (회원가입 및 다음 단계로 이동)
			btnJoin = new JButton("회원가입");
			btnNonJoin = new JButton("비회원으	로 구매");
			btnlogin = new JButton("로그인");
			btnJoin.addActionListener(this);
			btnNonJoin.addActionListener(this);
			btnlogin.addActionListener(this);

			JPanel pn3 = new JPanel();
			pn3.add(btnJoin);
			pn3.add(btnNonJoin);
			pn3.add(btnlogin);
			add("South", pn3);
		}
		
		private void accDb() {	//DB 접속
			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (Exception e) {
				System.out.println("accDb err: " + e);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
			// 회원/비회원 및 직원 접속 창 전환 (card layout)
			if (e.getSource() == btnCstm) {
				card.show(pn2, "Cs");
			} else if (e.getSource() == btnAdmin) {
				card.show(pn2, "Ad");
			}
			
			//로그인 시도
				
				//회원 로그인
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
				String sql = "SELECT memNo, password FROM passenger WHERE memNo=?";
				prep = conn.prepareStatement(sql);
				prep.setString(1, txtCsId.getText());
				
				rs = prep.executeQuery();
				
				
				String Id = null;
				String Pw = null;
				
				if(rs.next()) {
					Id = rs.getString(1);
					Pw = rs.getString(2);

					
					if (txtCsId.getText().equals(Id) && txtCsPw.getText().equals(Pw) && e.getSource() == btnlogin) {
						this.setVisible(false);
						new Flight_Purchase(txtCsId.getText());	//구매창으로 이동
						}
					//관리자 로그인
					
				
				}else if (txtAdminId.getText().equals("123") && txtAdminPw.getText().equals("123") && e.getSource() == btnlogin) {
					this.setVisible(false);
					new Flight_Admin();		//관리창으로 이동
				} else if(e.getSource() == btnlogin) {
					JOptionPane.showMessageDialog(this, "정확한 정보 입력");	//?
				} /*else if((!txtCsId.getText().equals("123") || txtAdminPw.getText().equals("123"))
						&& txtCsId.equals(null)) {
					JOptionPane.showMessageDialog(this, "정확한 정보 입력");	//?
				}*/
			
			} catch (Exception e2) {
				
			} finally { // DB 필요없을 땐 끊어 주는 것
				try {
					if (rs != null)
						rs.close();
					if (prep != null)
						prep.close();
					if (conn != null)
						conn.close();
				} catch (Exception e3) {
					// TODO: handle exception
				}
			}
			
			
			if(e.getSource() == btnJoin){
				Join_InsertForm join = new Join_InsertForm(this);
			}
			
			if(e.getSource() == btnNonJoin){
				JOptionPane.showMessageDialog(this, "서비스 준비 중입니다. 회원가입을 이용해주세요");
				return;
			} 

		}
	}
	
	class Join_InsertForm extends JDialog implements ActionListener{
//	JLabel no, name, gen, age, pw;
	JTextField no1, name1, ssc1, ssc2, pw1;
	
	JButton btnCncl = new JButton("취소");
	JButton btnOkk = new JButton("회원등록");
//	 memNo   | memName | memGen | memAge | password
	
	
	public Join_InsertForm(Frame frame) {
		
		super(frame, "회원가입");
		setModal(true);	//modal(sub창 닫기 전까지 다른 작업 못함) & modaless (다중 작업 가능)
						//modal은 JDialog에서 setVisible 대신 씀
		JPanel pn1 = new JPanel(new GridLayout(4,2));
		
		
		pn1.add(new JLabel("이름: ", JLabel.RIGHT));
		pn1.add(name1 = new JTextField(10));
		
		pn1.add(new JLabel("주민번호: ", JLabel.RIGHT));
		JPanel pn2 = new JPanel(new GridLayout(1,2));
		JPanel pn2_1 = new JPanel();
		pn2_1.add(ssc1 = new JTextField(6));
		pn2_1.add("EAST", new JLabel("-"));
		JPanel pn2_2 = new JPanel();
		pn2_2.add("WEST", ssc2 = new JTextField(1));
		pn2_2.add(new JLabel("*******"));
		pn2.add(pn2_1);
		pn2.add(pn2_2);
		pn1.add(pn2);
		
		pn1.add(new JLabel("비밀번호: ", JLabel.RIGHT));
		pn1.add(pw1 = new JTextField(10));

		pn1.add(btnOkk);
		pn1.add(btnCncl);
		
		btnOkk.addActionListener(this);
		btnCncl.addActionListener(this);
		
		add("North", new JLabel("회원등록 정보", JLabel.CENTER));
		add("Center",pn1);
		setBounds(500, 310, 400, 250);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();	//하나의 Frame만 종료 시키기 위해서는 dispose() 메소드를 사용하여야 한다.
			}
		});
	}
	
	@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btnCncl) {
				dispose();
			}
			
			if(e.getSource()==btnOkk) {
				try {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");

					// 회원정보 (하나씩 증가 - (MAX +1))
					int new_memNo = 0;

					String new_Gen = null;
					int new_Age = 0;

					// 구매코드번호 + 1 (자동생성)
					prep = conn.prepareStatement("SELECT MAX(SUBSTR(memNo,2)) FROM passenger");
					rs = prep.executeQuery();

					if (rs.next()) {
						new_memNo = rs.getInt(1);
					}
					
					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);
					String birthY = ssc1.getText().substring(0,2);
					int b_year = 0;
					if(ssc2.getText().equals("1") || ssc2.getText().equals("2")) b_year = Integer.parseInt(birthY) + 1900;
					else if(ssc2.getText().equals("3") || ssc2.getText().equals("4")) b_year = Integer.parseInt(birthY) + 2000;
					new_Age = year - b_year + 1; 
					
					if(ssc2.getText().equals("1") || ssc2.getText().equals("3")) {
						new_Gen = "남";
					} else if(ssc2.getText().equals("2") || ssc2.getText().equals("4")) {
						new_Gen = "여";
					}
					
					
					try {
//						no1, name1, ssc1, ssc2, pw1
	//					 memNo   | memName | memGen | memAge | password
							prep = conn.prepareStatement("INSERT INTO passenger VALUES(?,?,?,?,?)");
							prep.setString(1, "M" + (new_memNo + 1));
							prep.setString(2, name1.getText());
							prep.setString(3, new_Gen);
							prep.setString(4, Integer.toString(new_Age));
							prep.setString(5, pw1.getText());
	
							if (prep.executeUpdate() > 0) {
								JOptionPane.showMessageDialog(this, "가입 성공\n회원님의 번호는 M" + (new_memNo + 1) + "\n비밀번호는 " + pw1.getText() + "입니다. \n 꼭 기억하세요");
								System.out.println(name1.getText());
								System.out.println(year);
								System.out.println(b_year);
								System.out.println(new_Gen);
								System.out.println(Integer.toString(new_Age));
								System.out.println(pw1.getText());
	
								dispose();
							} else {
								JOptionPane.showMessageDialog(this, "등록 실패!");
							}
						} catch (Exception e3) {
							System.out.println("여기가 오류" + e3);
						}
					} catch (Exception e2) {

					} finally { // DB 필요없을 땐 끊어 주는 것
						try {
							if (rs != null)
								rs.close();
							if (prep != null)
								prep.close();
							if (conn != null)
								conn.close();
						} catch (Exception e3) {
							// TODO: handle exception
						}
					}
				
			}
		}
	
}

	public static void main(String[] args) {
		new FlightReserve();
	}

}
