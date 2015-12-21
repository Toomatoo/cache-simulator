package pku.ca.frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.BorderLayout;

import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JLabel;

import pku.ca.driver.*;

import javax.swing.JTree;

import java.awt.Component;

import javax.swing.Box;


public class WBswing {

	private JFrame frmCache;
	Driver driver = new Driver();
	private JTextField Filename;
	int sumofaccess;
	int hitime = 0;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WBswing window = new WBswing();
					window.frmCache.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WBswing() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCache = new JFrame();
		frmCache.setTitle("Cache Manager");
		frmCache.setBounds(100, 100, 527, 570);
		frmCache.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JLabel lblTag = new JLabel("Instruction");
		lblTag.setHorizontalAlignment(SwingConstants.CENTER);
		lblTag.setBounds(120, 59, 105, 16);
		frmCache.getContentPane().add(lblTag);
		
		JLabel lblOffset = new JLabel("Block Number");
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		lblOffset.setBounds(370, 107, 105, 22);
		frmCache.getContentPane().add(lblOffset);
		
		JLabel lblNewLabel = new JLabel("Block Select");
		lblNewLabel.setBounds(16, 87, 83, 16);
		frmCache.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Tag Match");
		lblNewLabel_1.setBounds(16, 167, 105, 16);
		frmCache.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Operating");
		lblNewLabel_2.setBounds(16, 252, 90, 16);
		frmCache.getContentPane().add(lblNewLabel_2);
		
		final JTextArea Blockselect = new JTextArea();
		Blockselect.setToolTipText("");
		Blockselect.setBounds(120, 87, 383, 16);
		frmCache.getContentPane().add(Blockselect);
		
		final JTextArea BlockNum = new JTextArea();
		BlockNum.setBounds(413, 131, 90, 16);
		frmCache.getContentPane().add(BlockNum);
		
		final JTextArea TagMatch = new JTextArea();
		TagMatch.setBounds(122, 167, 381, 16);
		frmCache.getContentPane().add(TagMatch);
		
		final JTextArea Group = new JTextArea();
		Group.setBounds(413, 205, 90, 16);
		frmCache.getContentPane().add(Group);
		
		final JTextArea Operating = new JTextArea();
		Operating.setBounds(120, 252, 383, 16);
		frmCache.getContentPane().add(Operating);
		
		final JTextArea Result = new JTextArea();
		Result.setBounds(413, 286, 90, 16);
		frmCache.getContentPane().add(Result);
		
		final JTextArea Instructions = new JTextArea();
		Instructions.setBounds(16, 344, 397, 164);
		frmCache.getContentPane().add(Instructions);
		
		final JTextArea HitRate = new JTextArea();
		HitRate.setBounds(425, 344, 78, 16);
		frmCache.getContentPane().add(HitRate);
		
		//Start Button
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent arg0) {	
				sumofaccess = 0;
				hitime = 0;
				HitRate.setText("");
				try {
					String filename = Filename.getText();
					driver.start(filename);
					Instructions.setText(driver.instrs);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		btnStart.setBounds(143, 6, 117, 29);
		frmCache.getContentPane().add(btnStart);
		
		//Next Cycle
		JButton btnNextCycle = new JButton("Next Cycle");
		btnNextCycle.setBounds(366, 6, 137, 29);
		btnNextCycle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
						
				//执行当前tick内容
				if(driver.tick < driver.boundtick) {
					driver.oneTick();	
					String outStr[] = outTheTextArea();
					
					//Block Select
					Blockselect.setText(outStr[0]);
					BlockNum.setText(outStr[1]);
					//Tag Match
					TagMatch.setText(outStr[2]);
					if(!(driver.tick == 0 || driver.tick == driver.arrayRequest.size()+1)
							&& driver.arrayRequest.get(driver.tick-1).op != 2) {
						if(driver.cachemanage.isHit) {
							hitime ++;
							Group.setText("Hit: "+outStr[3]);
						}
						else
							Group.setText("Miss: "+outStr[3]);
					}
					else
						Group.setText(outStr[3]);
					
					//Operate instruction
					Operating.setText(outStr[4]);
					//Operate result
					Result.setText(outStr[5]);
					if(driver.tick <driver.boundtick-2
							&& driver.arrayRequest.get(driver.tick).op != 2)
						sumofaccess ++;
					driver.tick ++;
					
					
				}
				else {
					//Operate instruction
					Operating.setText("");
					//Operate result
					Result.setText("");
					HitRate.setText(String.valueOf(hitime) + " / " + String.valueOf(sumofaccess));
				}
				return;
			}
		});
		frmCache.getContentPane().setLayout(null);
		frmCache.getContentPane().add(btnNextCycle);
				
		JLabel lblGroupNumber = new JLabel("Group Number");
		lblGroupNumber.setHorizontalAlignment(SwingConstants.LEFT);
		lblGroupNumber.setBounds(370, 182, 105, 22);
		frmCache.getContentPane().add(lblGroupNumber);
		
		JLabel lblResult = new JLabel("Result");
		lblResult.setHorizontalAlignment(SwingConstants.LEFT);
		lblResult.setBounds(370, 267, 105, 22);
		frmCache.getContentPane().add(lblResult);
		
		Filename = new JTextField();
		Filename.setText("FileName");
		Filename.setBounds(16, 5, 134, 28);
		frmCache.getContentPane().add(Filename);
		Filename.setColumns(10);
		
		JLabel lblHit = new JLabel("Hit Rate");
		lblHit.setBounds(413, 326, 61, 16);
		frmCache.getContentPane().add(lblHit);
		
		
		
	}
	
	String[] outTheTextArea() {
		String outStr[] = {"", "", "", "", "", ""};
		if(!(driver.tick == driver.arrayRequest.size()
				|| driver.tick == driver.arrayRequest.size()+1)) {
			//BlockSelect instruction
			outStr[0] = driver.AL_Str.get(driver.tick);
			//Block Number
			if(driver.arrayRequest.get(driver.tick).op != 2)
				outStr[1] = String.valueOf(driver.cachemanage.B2Tindex);
		}
		if(!(driver.tick == 0 || driver.tick == driver.arrayRequest.size()+1)) {
			//GroupSelect instruction
			outStr[2] = driver.AL_Str.get(driver.tick-1);
			//Group Number
			if(driver.arrayRequest.get(driver.tick-1).op != 2)
				outStr[3] = String.valueOf(driver.cachemanage.T2Ogroup);
		}
		if(!(driver.tick == 0 || driver.tick == 1)) {
			//Operate Instruction
			outStr[4] = driver.AL_Str.get(driver.tick-2);
			//Operate result
			if(driver.arrayRequest.get(driver.tick-2).op == 0)
				outStr[5] = "0x"+Integer.toHexString(driver.arrayResponse.get(driver.tick-2).data);
			else if(driver.arrayRequest.get(driver.tick-2).op == 1)
				outStr[5] = "Write success!";
			else 
				outStr[5] = "";
		}
		//Append to the Text Area
		return outStr;
	}
}
