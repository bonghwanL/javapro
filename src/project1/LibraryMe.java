package project1;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class LibraryMe extends JFrame implements ActionListener, MouseListener {
	ButtonGroup bgroup = new ButtonGroup();
	JRadioButton rbno, rbname, rwname, rbcom, rbyear, runo;

	JButton btnBs, btnCl, btnUs, btnUn, btnRt;
	String[][] bdatas = new String[0][7];
	String[] btitles = { "도서번호", "도서명", "저자명", "출판사", "출판년도", "대여상태", "이용자번호" };
	DefaultTableModel model = new DefaultTableModel(bdatas, btitles);
	JTable table = new JTable(model);
	JTextField st;

	Connection conn;
	PreparedStatement pstmt;
	Statement stmt;
	ResultSet rs, rs1;
	

	public LibraryMe() {
      super("도서정보관리시스템");

      layInit();
      accDb();

      setResizable(true);
      setBounds(400, 400, 1000, 500);
      setVisible(true);
      table.addMouseListener(this); 
      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            int re = JOptionPane.showConfirmDialog(LibraryMe.this, "정말 종료할까요?", "종료", JOptionPane.OK_CANCEL_OPTION);
            if (re == JOptionPane.OK_OPTION) {
               try {
                  if (rs != null)
                     rs.close();
                  if (conn != null)
                     conn.close();
                  if (pstmt != null)
                     pstmt.close();
               } catch (Exception e2) {
                  System.out.println("windowClosing err :" + e);
               }
               System.exit(0);
            } else {
               setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
         }
      });

   }

	private void layInit() {
		// 1 north 검색
		rbno = new JRadioButton("도서번호", true);
		rbname = new JRadioButton("도서명", false);
		rwname = new JRadioButton("저자명", false);
		rbcom = new JRadioButton("출판사", false);
		rbyear = new JRadioButton("출판년도", false);
		runo = new JRadioButton("이용자번호", false);
		bgroup.add(rbno);
		bgroup.add(rbname);
		bgroup.add(rwname);
		bgroup.add(rbcom);
		bgroup.add(rbyear);
		bgroup.add(runo);
		JLabel lbl1 = new JLabel();
		st = new JTextField("", 20);

		btnBs = new JButton("검색"); // 도서검색버튼
		btnBs.setBackground(Color.WHITE);
		btnBs.addActionListener(this);
		btnCl = new JButton("초기화"); // 도서검색 초기화
		btnCl.setBackground(Color.WHITE);
		btnCl.addActionListener(this);

		JPanel pn1 = new JPanel();
		pn1.add(rbno);
		pn1.add(rbname);
		pn1.add(rwname);
		pn1.add(rbcom);
		pn1.add(rbyear);
		pn1.add(runo);
		pn1.add(lbl1);
		pn1.add(st);
		pn1.add(btnBs);
		pn1.add(btnCl);
		add("North", pn1);

		// 2 center 리스트
		table.getColumnModel();
		JScrollPane scl = new JScrollPane(table);
		add("Center", scl);

		// 3 south
		
		
		

	}

	private void accDb() {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
			// 자료가 실행하자마자 보여지기
			dispData();
		} catch (Exception e) {
			System.out.println("accDb err : " + e);
		}
	}

	private void dispData() {
		model.setNumRows(0);
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
			String sql = "SELECT bno, bname, wname, bcom, byear, nvl2(uno, '대여중', '대여가능') AS 대여여부, bookinfo_uno\r\n"
					+ "FROM bookinfo\r\n" + "LEFT OUTER JOIN userinfo on bookinfo_uno = uno";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery(sql);

			while (rs.next()) {
				String[] binfo = { rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7) };
				model.addRow(binfo);
			}
		} catch (Exception e) {
			System.out.println("dispData err :" + e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (conn != null)
					conn.close();
				if (pstmt != null)
					pstmt.close();

			} catch (Exception e2) {
				System.out.println("dispData1 err :" + e2);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		int uno = 0;
		int bno = 0;
		// 대여
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
			String sql = "UPDATE bookinfo SET  bookinfo_uno = ?  WHERE bno = ?";
			pstmt = conn.prepareStatement(sql);
			
			
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();
			
			uno = (int)table.getValueAt(row, 6);
			bno = (int)table.getValueAt(row, 0);
			
			String rent = "";
			rent = (String)table.getValueAt(row, 5);
			
			if (rent.equals("X")) {
				JOptionPane.showMessageDialog(this, "대여 실패!" + "\n" + "이미 다른 이용자가 대여 중 입니다!");
				return;
			} else {
			//	JOptionPane.showMessageDialog(this,
			//			"대여 성공!" + "\n 이용자 " + + "번이 도서" + txtBno.getText() + "번을 대여하였습니다!");
					return;
				
			}

		} catch (Exception e2) {
			System.out.println("마지막 err : " + e2);
		}
		
		/*
		 * try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "123");

				TableModel data = table.getModel();
				String sql;
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				id = (String) data.getValueAt(row, 4);
				String code = (String) data.getValueAt(row, 0);
				BookId = (String) data.getValueAt(row, 1);
				if (id.equals("X")) {
					JOptionPane.showMessageDialog(this, "대여가 불가능합니다.");
					return;
				} else {
					int result = JOptionPane.showConfirmDialog(this, "대여하시겠습니까?", "대여 창", JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.YES_OPTION) {
						sql = "update book set rent = 'X', gogek_id = '" + GogekId + "' where code = '" + code + "'";
						pstmt = conn.prepareStatement(sql);
						rs = pstmt.executeQuery();
						dispData();
						Gogek g = new Gogek(this);
					}
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, "대여는 한권만 가능합니다.");
				return;
			}
		 */
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean mouseDown(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseDown(evt, x, y);
	}
	@Override
	public boolean mouseDrag(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseDrag(evt, x, y);
	}@Override
	public boolean mouseEnter(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseEnter(evt, x, y);
	}@Override
	public boolean mouseExit(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseExit(evt, x, y);
	}@Override
	public boolean mouseMove(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseMove(evt, x, y);
	}@Override
	public boolean mouseUp(Event evt, int x, int y) {
		// TODO Auto-generated method stub
		return super.mouseUp(evt, x, y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
			stmt = conn.createStatement();
			String sql = "SELECT bno, bname, wname, bcom, byear, nvl2(uno, '대여중', '대여가능') AS 대여여부, bookinfo_uno\r\n"
					+ "FROM bookinfo\r\n" + "LEFT OUTER JOIN userinfo on bookinfo_uno = uno";

			if (e.getSource() == btnBs) {

				// 입력 유효성 검사, 입력된 검색어가 없을때.
				if (st.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "정보를 입력하세요");
					st.requestFocus();
					return;

				} else { // 입력된 검색어가 있을때.
					if (rbno.isSelected()) {// 도서번호으로 검색 조건
						sql += " where bno like '%" + st.getText().trim() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}

					} else if (rbname.isSelected()) {// 도서명 검색
						sql += " where bname like '%" + st.getText() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}

					} else if (rwname.isSelected()) { // 저자명으로 검색 조건
						sql += " where wname like '%" + st.getText() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}

					} else if (rbcom.isSelected()) {// 출판사로 검색 조건
						sql += " where bcom like '%" + st.getText() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}
					} else if (rbyear.isSelected()) {// 년도로 검색 조건
						sql += " where byear like '%" + st.getText() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}
					} else if (runo.isSelected()) {// 출판사로 검색 조건
						sql += " where bookinfo_uno like '%" + st.getText() + "%'";

						rs1 = stmt.executeQuery(sql);

						if (rs1.absolute(0) == true) {// 대조해본 결과 행의 값이 있을때 실행
							model.setNumRows(0);
							while (rs1.next()) {
								String[] binfo = { rs1.getString(1), rs1.getString(2), rs1.getString(3),
										rs1.getString(4), rs1.getString(5), rs1.getString(6), rs1.getString(7) };
								model.addRow(binfo);
							}

						} else {// 없을 때 실행
							JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
							st.setText("");
							return;
						}
					}

				}
			} else if (e.getSource() == btnCl) {
				model.setNumRows(0);
				dispData();
				st.setText("");
				st.requestFocus(); // 도서넘버로 초기화
				return;
			} else if (e.getSource() == btnRt) { // 이용자신규
				BookRt BookRt = new BookRt(this);
			}

		} catch (Exception e2) {
			System.out.println("자료검색 오류: " + e2);
		}
	}

////// ============================================
	class BookRt extends JDialog implements ActionListener {
		JButton brent = new JButton("대여");
		JButton breturn = new JButton("반납");

		JTextField txtUno = new JTextField("", 10);
		JTextField txtBno = new JTextField("", 10);

		Connection conn;
		PreparedStatement pstmt;
		ResultSet rsr;

		public BookRt(Frame frame) {
			super(frame, "도서 대여/반납");
			accDb();

			setModal(true);
			setResizable(true);

			setLayout(new GridLayout(3, 1));

			JPanel pn1 = new JPanel();
			pn1.add(new JLabel("이용자 번호 : ", JLabel.LEFT));
			pn1.add(txtUno);
			add("North", pn1);

			JPanel pn2 = new JPanel();
			pn2.add(new JLabel("도서 번호 : ", JLabel.LEFT));
			pn2.add(txtBno);
			add("Center", pn2);

			JPanel pn3 = new JPanel();
			brent.setBackground(Color.white);
			pn3.add(brent);
			breturn.setBackground(Color.white);
			pn3.add(breturn);
			brent.addActionListener(this);
			breturn.addActionListener(this);
			add("South", pn3);

			txtUno.setEditable(true);
			txtBno.setEditable(true);

			setBounds(450, 450, 300, 300);
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
				System.out.println("accDb err : " + e);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == brent) {
				// 입력자료 오류 검사
				if (txtUno.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "이용자번호 입력");
					txtUno.requestFocus();
					return;
				} else if (txtBno.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "도서번호 입력");
					txtBno.requestFocus();
					return;
				}

				// 대여여부 확인
				int bno = 0;
				try {
					bno = Integer.parseInt(txtBno.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, "도서번호는 숫자만 입력 가능");
					txtBno.setText("");
				}
				int uno = 0; // null 아니면
				try {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
					String sql = "SELECT bookinfo_uno FROM bookinfo LEFT OUTER JOIN userinfo on bookinfo_uno = uno WHERE bno = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, bno);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						uno = rs.getInt(1);
					}

					if (uno != 0) {
						JOptionPane.showMessageDialog(this, "대여불가능.");
						txtUno.setText("");
						txtBno.setText("");
						return;
					}

				} catch (Exception e2) {
					System.out.println("대여 불가능" + e2);
					txtUno.setText("");
					txtBno.setText("");
					return;
				}

				// 대여
				uno = Integer.parseInt(txtUno.getText());
				try {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
					String sql = "UPDATE bookinfo SET  bookinfo_uno = ?  WHERE bno = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, uno);
					pstmt.setInt(2, bno);

					if (pstmt.executeUpdate() > 0) {
						JOptionPane.showMessageDialog(this,
								"대여 성공!" + "\n 이용자 " + txtUno.getText() + "번이 도서" + txtBno.getText() + "번을 대여하였습니다!");
						txtUno.setText("");
						txtBno.setText("");
						return;
					} else {
						JOptionPane.showMessageDialog(this, "대여 실패!" + "\n" + "이미 다른 이용자가 대여 중 입니다!");
						txtBno.setText("");

						return;
					}

				} catch (Exception e2) {
					System.out.println("마지막 err : " + e2);
				}

			}
			if (e.getSource() == breturn) {
				// 입력자료 오류 검사
				if (txtUno.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "이용자번호 입력");
					txtUno.requestFocus();
					return;
				} else if (txtBno.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "도서번호 입력");
					txtBno.requestFocus();
					return;
				}

				int uno = 0;
				try {
					uno = Integer.parseInt(txtUno.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, "이용자번호는 숫자만 입력 가능");
					txtUno.setText("");
				}

				// 반납 여부 확인
				int bno = 0;// null 아니면
				try {
					bno = Integer.parseInt(txtBno.getText());
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, "도서번호는 숫자만 입력 가능");
					txtBno.setText("");
				}
				try {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
					String sql = "SELECT bno FROM bookinfo LEFT OUTER JOIN userinfo on bookinfo_uno = uno WHERE bookinfo_uno = ? and bno=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, uno);
					pstmt.setInt(2, bno);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						bno = rs.getInt(1);
					}
					if (bno == 0) {
						JOptionPane.showMessageDialog(this, "이용자" + txtUno.getText() + "번이 대여한 책이 아닙니다!.");

						txtBno.setText("");
						return;
					}

				} catch (Exception e2) {
					System.out.println("반납여부 불가능" + e2);
					txtUno.setText("");
					txtBno.setText("");
					return;
				}
				uno = Integer.parseInt(txtUno.getText());
				bno = Integer.parseInt(txtBno.getText());
				// 반납
				try {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "123");
					String sql = "UPDATE bookinfo SET bookinfo_uno = Null WHERE bookinfo_uno=? and bno = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, uno);
					pstmt.setInt(2, bno);

					if (pstmt.executeUpdate() > 0) {
						JOptionPane.showMessageDialog(this,
								"이용자" + txtUno.getText() + "번이 도서" + txtBno.getText() + "번 반납 성공!");
						txtUno.setText("");
						txtBno.setText("");
						return;
					} else {
						JOptionPane.showMessageDialog(this, "대여한 책이 아닙니다! 반납 실패!");
						txtBno.setText("");
						return;
					}

				} catch (Exception e2) {
					System.out.println("마지막 err : " + e2);
				}
			}

		}
	}

	public static void main(String[] args) {
		new LibraryMe();

	}

}