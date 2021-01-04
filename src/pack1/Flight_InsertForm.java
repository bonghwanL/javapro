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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Flight_InsertForm extends JDialog implements ActionListener{
	JTextField txt1 = new JTextField();
	JTextField txt2 = new JTextField();
	JTextField txt3 = new JTextField();
	
	JButton btnOk = new JButton("등록");
	JButton btnCncl = new JButton("초기화");
	
	Connection conn;
	PreparedStatement prep;
	ResultSet rs;
	
	String title, s1, s2, s3;
	String sql1, sql2;

	public Flight_InsertForm(Frame frame, String title, String s1, String s2, String s3) {
		super(frame, title);
		setModal(true);	//modal(sub창 닫기 전까지 다른 작업 못함) & modaless (다중 작업 가능)
		
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		
		JPanel pn1 = new JPanel(new GridLayout(4,2));
		
		pn1.add(new JLabel(s1));
		pn1.add(txt1);
		
		pn1.add(new JLabel(s2));
		pn1.add(txt2);
		
		pn1.add(new JLabel(s3));
		pn1.add(txt3);

		pn1.add(btnOk);
		pn1.add(btnCncl);
		
		btnOk.addActionListener(this);
		btnCncl.addActionListener(this);
		
		add("North", new JLabel("자료 입력하기", JLabel.CENTER));
		add("Center",pn1);
		
		setBounds(310, 310, 300, 150);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();	//하나의 Frame만 종료 시키기 위해서는 dispose() 메소드를 사용하여야 한다.
			}
		});
	}
	
	
	public void ok(String sql, String sql2) {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3310/test", "root", "qhdghks5");
			
			//신상 code 구하기 (하나씩 증가 - (MAX +1))
			int new_code = 0;
//			sql = "SELECT MAX(code) FROM sangdata";
			prep = conn.prepareStatement(sql);
			rs = prep.executeQuery();
			
			if(rs.next()) {
				new_code = rs.getInt(1);
			}
//			System.out.println(new_code + 1);
			
//			sql2 = "INSERT INTO sangdata VALUES(?,?,?,?)";	//수정?
			prep = conn.prepareStatement(sql);
			prep.setInt(1, new_code + 1);
			prep.setString(2, txt1.getText().trim());	//trim(): 앞 뒤 공백지우기 (cf.rtrim, ltrim) 
			prep.setString(3, txt2.getText().trim());	//이미 위에서 숫자로 바꿈
			prep.setString(4, txt3.getText().trim());	//이미 위에서 숫자로 바꿈
			
			if(prep.executeUpdate() > 0) {
				JOptionPane.showMessageDialog(this, "등록 성공\n" );
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "등록 실패!");
			}
			
		} catch (Exception e2) {
			
		} finally {	//DB 필요없을 땐 끊어 주는 것
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
	
	public void ok(String sql) {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnOk) {	//신상품 등록 추가
			//입력자료 검사
			if(txt1.getText().equals("")) {
				JOptionPane.showMessageDialog(this, s1 + "입력");
				txt1.requestFocus();
				
				return;	//이후 작업 못하게 함
			} else if(txt2.getText().equals("")) {
				JOptionPane.showMessageDialog(this, s2 + "입력");
				txt2.requestFocus();
				
				return;	//이후 작업 못하게 함
			} else if(txt3.getText().equals("")) {
				JOptionPane.showMessageDialog(this, s3 + "입력");
				txt3.requestFocus();
				
				return;	//이후 작업 못하게 함
			}
			
			//수량, 단가는 숫자 (유효성 검사)
			int su = 0;
//			try {
//				su = Integer.parseInt(txt2.getText());
//			} catch (Exception e2) {
//				JOptionPane.showMessageDialog(this, "은 숫자만 가능");
//				txt2.requestFocus();
//				
//				return;	//이후 작업 못하게 함
//			}
			
			int dan = 0;
			try {
				dan = Integer.parseInt(txt3.getText());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(this, s3 + "는 숫자만 가능");
				txt3.requestFocus();
				
				return;	//이후 작업 못하게 함
			}
			
			//등록 가능한 상태
//			ok(sql1,sql2);
			
		} else if(e.getSource()==btnCncl) {	//입력자료 초기화
			txt1.setText("");
			txt2.setText("");
			txt3.setText("");
			txt1.requestFocus();
		}
	}
}

