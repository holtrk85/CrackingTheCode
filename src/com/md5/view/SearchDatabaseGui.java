package com.md5.view ;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.md5.database.Database;
import com.md5.database.Database.SearchResult;
import com.md5.generate.PairGenerationController;
import com.md5.util.Utilities;

/**
 * Simple class to provide a GUI interface to search the password database.ja
 */
public class SearchDatabaseGui implements ActionListener 
{  
	private class PasswordChangeListener implements DocumentListener
	{
		public void changedUpdate(DocumentEvent e) 
    	{
    		warn();
    	}
    	public void removeUpdate(DocumentEvent e) 
    	{
    		warn();
    	}
    	public void insertUpdate(DocumentEvent e) 
    	{
    		warn();
    	}

    	public void warn() 
    	{
    		String password = _passwordText.getText().trim() ;
    		if( password == null ||  password.equals("") )
    		{
    			_md5Text.setText("") ;
           	    _locationText.setText("") ;
           	    _searchResult = null ;
    		}
    		else
    		{
    		     String md5 = Utilities.getMd5Hash( password ) ;
    		     _md5Text.setText( md5 ) ;
                 _searchResult = _database.search( md5 );
                 
                 if( _searchResult.success() )
                 {
                     _locationText.setText( _searchResult.getLocation() ) ;
                 }
                 else
                 {
                	 _locationText.setText( "NO MATCH" ) ;
                 }
    		}
    		
    		_addButton.setEnabled( _searchResult != null && _searchResult.success() == false ) ;
    	}
	}
	
    private final Database _database ;
    private final PairGenerationController _pairGenerator ;
    
    private JTextField _passwordText;
    private JTextField _md5Text;
    private JTextField _locationText;
    private JButton _addButton;
    private JButton _exitButton;
	private SearchResult _searchResult ;

    public SearchDatabaseGui()
    {
    	_database = new Database() ;
    	_pairGenerator = new PairGenerationController( _database ) ;
    	
        // Creating instance of JFrame
        JFrame frame = new JFrame("Search Password Database");
        frame.setSize(425, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window.
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        
        // Creating panel. This is same as a div tag in HTML
        // We can create several panels and add them to specific 
        // positions in a JFrame. Inside panels we can add text 
        // fields, buttons and other components.
        
        JPanel panel = new JPanel();    
        frame.add(panel);
        
        // Adding components.
        
        _buildGui(panel);

        // Setting the frame visibility to true
        frame.setVisible(true);
    }

    private void _buildGui(JPanel panel) 
    {
        panel.setLayout(null);
        
        int y = 20 ;

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10,y,80,25);
        panel.add(passwordLabel);
        _passwordText = new JTextField(20);
        _passwordText.setBounds(100,y,305,25);
        _passwordText.getDocument().addDocumentListener( new PasswordChangeListener() ) ;
        panel.add(_passwordText);
                
        y+=30 ;
        
        // MD5 Hash
        JLabel md5Label = new JLabel("MD5 Hash");
        md5Label.setBounds(10,y,80,25);
        panel.add(md5Label);
        _md5Text = new JTextField(20);
        _md5Text.setBounds(100,y,305,25);
        panel.add(_md5Text);

        y+=30 ;

        // Location.
        JLabel locationLabel = new JLabel("Location");
        locationLabel.setBounds(10,y,80,25);
        panel.add(locationLabel);
        _locationText = new JTextField(20);
        _locationText.setBounds(100,y,305,25);
        panel.add(_locationText);

        y+=35 ;

        // Add button
        _addButton = new JButton(" Add ");
        _addButton.setBounds(120, y, 80, 25);
        panel.add(_addButton);
        _addButton.addActionListener(this);
        _addButton.setEnabled( false ) ;
        
        // Exit button
        _exitButton = new JButton("Exit");
        _exitButton.setBounds(220, y, 80, 25);
        panel.add(_exitButton);
        _exitButton.addActionListener(this);
        
        y+=35 ;
        
        JLabel databaseLabel = new JLabel( "Database:" ) ;
        databaseLabel.setBounds(10,y,425,25);
        JLabel databasePathLabel = new JLabel( _database.getDatabasePath() ) ;
        databasePathLabel.setBounds(10,y+15,425,25);
        panel.add(databaseLabel);
        panel.add(databasePathLabel);

    }
    
    @Override
    public void actionPerformed( ActionEvent event ) 
    {
        if( event.getSource() == _exitButton )
        { 
            System.exit(0) ;
        }
        else if( event.getSource() == _addButton )
        { 
        	int newPairCount = _pairGenerator.estimateNewPairCount( _passwordText.getText() ) ;
            String message = "You are about to add to the database. Please verify.\n" +
        	                 "Clicking OK will add " + newPairCount + " new pairs to the database.";
            int add = JOptionPane.showConfirmDialog(null, message, "Add to Database", JOptionPane.OK_CANCEL_OPTION ) ;
            if( add == JOptionPane.OK_OPTION )
            {
                _pairGenerator.generatePairs( _passwordText.getText() ) ;
            }
        }
    }
    
    public static void main(String[] args) 
    {   
        new SearchDatabaseGui() ;
    }
}