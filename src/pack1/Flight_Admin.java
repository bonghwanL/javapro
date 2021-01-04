package pack1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Flight_Admin extends JFrame implements ActionListener{

	JButton btnIns, btnUpd, btnDel, btnExt, btnNext, btnPrev;
	String[][] datas = new String[0][7];
	String[] titles = { "회원번호", "이름", "성별", "나이", "티켓번호", "구매날짜", "구매가격"};
	DefaultTableModel model = new DefaultTableModel(datas, titles); // 멤버선언할 때 미리 입력 layout 배치 때 안해도 됨
	JTable table = new JTable(model);
	JLabel lblCount = new JLabel("건수: 0");

	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;

	public Flight_Admin() {
		super("데이터베이스");

		layoutInit();
		accDb();

		
		setResizable(false);
		setBounds(300, 300, 600, 300);
		setVisible(true);


		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				int re = JOptionPane.showConfirmDialog(Flight_Admin.this, "정말 종료할까요?", "종료",
						JOptionPane.OK_CANCEL_OPTION);

				if (re == JOptionPane.OK_OPTION) {
					try {
						if (rs != null)
							rs.close();
						if (pstmt != null)
							pstmt.close();
						if (conn != null)	
							conn.close();
					} catch (Exception e2) {

					}
					setDefaultCloseOperation(EXIT_ON_CLOSE);
					//System.exit(0);
				} else {
					setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				}
			}
		});

	}
	private void layoutInit() {
//		btnIns = new JButton("추가");
//		btnUpd = new JButton("수정");
//		btnDel = new JButton("삭제");
		btnExt = new JButton("종료");

//		btnIns.addActionListener(this);
//		btnUpd.addActionListener(this);
//		btnDel.addActionListener(this);
		btnExt.addActionListener(this);

		JPanel panel = new JPanel();
//		panel.add(btnIns);
//		panel.add(btnUpd);
//		panel.add(btnDel);
		panel.add(btnExt);

		add("North", panel);

		table.getColumnModel().getColumn(0).setPreferredWidth(30); // 테이블의 열의 폭 조정
		JScrollPane scroll = new JScrollPane(table);

		add("Center", scroll);

		add("South", lblCount);

	}
	
	private void accDb() {
		try { // 추가 할 떄, 삭제 할 때 각 각 불러야 되기 때문
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
			String sql = "SELECT memNo, memName, memGen, memAge, TICKET_ticketNo, purDate, purPrice FROM passenger LEFT JOIN purchase ON memNo = PASSENGER_memNo"; // mariaDB는 Index 기본 Order by 되어 있음
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			int count = 0;

			while (rs.next()) {
				String[] temp = { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7) };
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
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {

			}
		}
	}

	private void dispData() {
		model.setNumRows(0); // table 초기화

		try { // 추가 할 떄, 삭제 할 때 각 각 불러야 되기 때문
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
			String sql = "SELECT * FROM sangdata"; // mariaDB는 Index 기본 Order by 되어 있음
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			int count = 0;

			while (rs.next()) {
				String[] temp = { rs.getString("code"), rs.getString("sang"), rs.getString("su"), rs.getString("dan") };
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
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {

			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnIns) {	//상품 추가
			Flight_InsertForm insertform = new Flight_InsertForm(this,"데이터 추가", "티켓번호", "승객", "출발날자");	//modal 이라서 insertform 불러오지 않으면 상품 등록 불가 //this 의 의미?
			dispData(); 	//추가 후 목록 보기
		}
//		else if(e.getSource()==btnUpd) {	//상품 수정 (생략) → 추가랑 다른 점: 선택한 줄의 데이터 프레임에 미리 출력됨
//			
//		}
		else if(e.getSource()==btnDel) {	//상품 삭제
			String del = JOptionPane.showInputDialog(this, "삭제할 코드 번호 입력");	//input 넣고 사라지는 프레임 창
			if (del == null) return;
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");

				String sql = "DELETE FROM sangdata WHERE code=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, del);
				
				if(pstmt.executeUpdate() == 0) {
					JOptionPane.showMessageDialog(this, "삭제 가능한 상품이 없습니다.");
					return;
				}
//				else { } 안써도 이미 update 실행 된것
				JOptionPane.showMessageDialog(this, "삭제되었습니다");
				dispData();
			} catch (Exception e2) {
				System.out.println("삭제 오류: " + e2);
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						conn.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		} else if(e.getSource()==btnExt) {	//상품 종료
			int re = JOptionPane.showConfirmDialog(Flight_Admin.this, "정말 종료할까요?", "종료",
					JOptionPane.OK_CANCEL_OPTION);

			if (re == JOptionPane.OK_OPTION) {
				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)	
						conn.close();
				} catch (Exception e2) {

				}
				System.exit(0);
			} else {
				setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}
		}
	}


}
