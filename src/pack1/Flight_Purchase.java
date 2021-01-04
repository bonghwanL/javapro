package pack1;

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

public class Flight_Purchase extends JFrame implements ActionListener {

	JButton btnBuy, btnLook;
	String[][] datas = new String[0][7]; // 수정?
	String[] titles = { "티켓번호", "출발지", "출발일자", "도착지", "도착일자", "가격", "편명" };// 수정?
	DefaultTableModel model = new DefaultTableModel(datas, titles); // 멤버선언할 때 미리 입력 layout 배치 때 안해도 됨
	JTable table = new JTable(model);
	JLabel lblCount = new JLabel("건수: 0");

	Connection conn;
	PreparedStatement prep;
	ResultSet rs;

	String info;

	public Flight_Purchase(String info) {

		super("오늘의 특가");

		this.info = info;

		layoutInit();
		accDb();

		setResizable(false);
		setBounds(300, 300, 600, 300);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int re = JOptionPane.showConfirmDialog(Flight_Purchase.this, "정말 종료할까요?", "종료",
						JOptionPane.OK_CANCEL_OPTION);
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
					System.exit(0);
				} else {
					setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				}
			}
		});

	}

	private void layoutInit() {
		btnBuy = new JButton("비행기 표 구입");
		btnLook = new JButton("구매 내역 조회");

		btnBuy.addActionListener(this);
		btnLook.addActionListener(this);

		JPanel panel = new JPanel();
		panel.add(btnBuy);
		panel.add(btnLook);

		add("South", panel);

		table.getColumnModel().getColumn(0).setPreferredWidth(30); // 테이블의 열의 폭 조정
		JScrollPane scroll = new JScrollPane(table);

		add("Center", scroll);

	}

	private void accDb() {
		try { // 추가 할 떄, 삭제 할 때 각 각 불러야 되기 때문
			Class.forName("org.mariadb.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
			String sql = "SELECT * FROM ticket"; // mariaDB는 Index 기본 Order by 되어 있음
			prep = conn.prepareStatement(sql);
			rs = prep.executeQuery();

			int count = 0;
//			ticketNo | deptPlace | deptDate   | arrivalPlace | arrivalDate | ticketPrice | flightNo
			while (rs.next()) {
				String[] temp = { rs.getString("ticketNo"), rs.getString("deptPlace"), rs.getString("deptDate"),
						rs.getString("arrivalPlace"), rs.getString("arrivalDate"), rs.getString("ticketPrice"),
						rs.getString("flightNo") };
				model.addRow(temp);
				count++;
			}
			lblCount.setText("건수: " + count);
			
		} catch (Exception e) {
			System.out.println("dispData err: " + e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (prep != null)
					prep.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {

			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnBuy) {
			Purchase_InsertForm insertform = new Purchase_InsertForm(this, "비행기 티켓 구매", "티켓번호", "회원번호", info);
//			

		} else if (e.getSource() == btnLook) {
			try { // 추가 할 떄, 삭제 할 때 각 각 불러야 되기 때문
				Class.forName("org.mariadb.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
				String sql = "SELECT * FROM purchase WHERE PASSENGER_memNo=?"; // mariaDB는 Index 기본 Order by 되어 있음
				prep = conn.prepareStatement(sql);
				prep.setString(1, info);
				rs = prep.executeQuery();

					if (rs.next()) {
						Purchase_InsertForm2 insertform2 = new Purchase_InsertForm2(info);
					}else JOptionPane.showMessageDialog(this, "구매한 내역이 없습니다");
			} catch (Exception e1) {
				
			
			}

		}
	}

}

class Purchase_InsertForm2 extends JDialog {

	String[][] datas = new String[0][6];
	String[] titles = { "구매번호", "비행기편", "티켓번호", "회원정보", "구매날짜", "구매가격" };

	DefaultTableModel model = new DefaultTableModel(datas, titles); // 멤버선언할 때 미리 입력 layout 배치 때 안해도 됨
	JTable table = new JTable(model);
	JLabel lblCount = new JLabel("건수: 0");

	Connection conn;
	PreparedStatement prep;
	ResultSet rs;
	
	String info;
	
	public Purchase_InsertForm2(String info) {
		JPanel pn1 = new JPanel(new GridLayout(2, 6));
		setTitle("구매내역");
		
		this.info = info;

		table.getColumnModel().getColumn(0).setPreferredWidth(30); // 테이블의 열의 폭 조정
		JScrollPane scroll = new JScrollPane(table);

		add("Center", scroll);
		
		accDb2();

		setBounds(310, 310, 400, 150);
		setVisible(true);
		

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose(); // 하나의 Frame만 종료 시키기 위해서는 dispose() 메소드를 사용하여야 한다.
			}
		});

	}

	private void accDb2() {
		try { // 추가 할 떄, 삭제 할 때 각 각 불러야 되기 때문
			Class.forName("org.mariadb.jdbc.Driver");

			dispData();

		} catch (Exception e1) {

		}
	}

	private void dispData() {
		model.setNumRows(0); // table 초기화

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
			String sql = "SELECT * FROM purchase WHERE PASSENGER_memNo=?"; // mariaDB는 Index 기본 Order by 되어 있음
			prep = conn.prepareStatement(sql);
			prep.setString(1, info);
			rs = prep.executeQuery();
			
			int count = 0;
			
				while (rs.next()) {
					String[] temp = { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6) };
					model.addRow(temp);
					count++;
				}
				lblCount.setText("건수: " + count);
				
		} catch (Exception e) {
			
		}
	}
	
}

class Purchase_InsertForm extends JDialog implements ActionListener {
	JTextField txt1 = new JTextField();
//	JTextField txt2 = new JTextField();
//	JTextField txt3 = new JTextField();

	JButton btnOk = new JButton("등록");
	JButton btnCncl = new JButton("초기화");

	Connection conn;
	PreparedStatement prep;
	ResultSet rs;

	String title, s1, s2, info;
	String sql1, sql2;

	public Purchase_InsertForm(Frame frame, String title, String s1, String s2, String info) {
		super(frame, title);
		setModal(true); // modal(sub창 닫기 전까지 다른 작업 못함) & modaless (다중 작업 가능)

		this.s1 = s1;
		this.s2 = s2;
//		this.s3 = s3;

		JPanel pn1 = new JPanel(new GridLayout(4, 2));

		pn1.add(new JLabel(s1));
		pn1.add(txt1);

		pn1.add(new JLabel(s2));
//		pn1.add(txt2);
		this.info = info;
		pn1.add(new JLabel(info));

//		pn1.add(new JLabel(s3));
//		pn1.add(txt3);

		pn1.add(btnOk);
		pn1.add(btnCncl);

		btnOk.addActionListener(this);
		btnCncl.addActionListener(this);

		add("North", new JLabel("자료 입력하기", JLabel.CENTER));
		add("Center", pn1);

		setBounds(310, 310, 300, 150);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose(); // 하나의 Frame만 종료 시키기 위해서는 dispose() 메소드를 사용하여야 한다.
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOk) {
			// 입력자료 검사
			if (txt1.getText().equals("")) {
				JOptionPane.showMessageDialog(this, s1 + "입력");
				txt1.requestFocus();

				return; // 이후 작업 못하게 함
			}

			// 등록 가능한 상태
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");

				// 신상 code 구하기 (하나씩 증가 - (MAX +1))
				int new_purNo = 0;

				String new_memNo = null;
				String new_ticketNo = null;
				String new_flightNo = null;
				String new_ticketPrice = null;

				// 구매코드번호 + 1 (자동생성)
				prep = conn.prepareStatement("SELECT MAX(purNo) FROM purchase");
				rs = prep.executeQuery();

				if (rs.next()) {
					new_purNo = rs.getInt(1);
				}

				prep = conn.prepareStatement("SELECT flightNo, ticketPrice FROM ticket WHERE ticketNo = ?");
				prep.setString(1, txt1.getText().trim());
				rs = prep.executeQuery();

				if (rs.next()) {
					new_flightNo = rs.getString(1);
					new_ticketPrice = rs.getString(2);
				}
				try {
					
					Calendar cal = Calendar.getInstance();
					int year = cal.get(Calendar.YEAR);
					int month = cal.get(Calendar.MONTH)+1 ;
					int day = cal.get(Calendar.DAY_OF_MONTH);
					String date = Integer.toString(year) +  "0"+ Integer.toString(month) + "0"+ Integer.toString(day);
					
//				purNo | FLIGHT_flightNo | TICKET_ticketNo | PASSENGER_memNo | purDate    | purPrice
					prep = conn.prepareStatement("INSERT INTO purchase VALUES(?,?,?,?,?,?)");
					prep.setInt(1, new_purNo + 1);
					prep.setString(2, new_flightNo);
					prep.setString(3, txt1.getText());
					prep.setString(4, info);
					prep.setString(5, date);
					prep.setString(6, new_ticketPrice);

					if (prep.executeUpdate() > 0) {
						JOptionPane.showMessageDialog(this, "구매 성공\n");
						System.out.println(new_flightNo);
						System.out.println(txt1.getText());
						System.out.println(info);
						System.out.println(new_ticketPrice);

						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "구매 실패!");
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

		} else if (e.getSource() == btnCncl) { // 입력자료 초기화
			txt1.setText("");
//			txt2.setText("");
//			txt3.setText("");
			txt1.requestFocus();
		}
	}
}
