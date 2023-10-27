//-----------For Java Swing---------------
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellRenderer;
//-----------For File reading and writing---------------
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//------------------For making pie chart----------------------
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
//-------------Miscellaneous--------------
import java.util.Random;

//DONE(100%)
class mainFrame extends JFrame
{
    private DefaultTableModel table;
    private JTable tab;
    private int row;
    public mainFrame()
    {
        MakeGUI();
    }
    public void MakeGUI()
    {
        setTitle("Library Management.");
        setLayout(new BorderLayout());
        setSize(1000,700);
        table = new DefaultTableModel();
        table.addColumn("Title");
        table.addColumn("Author");
        table.addColumn("Publication");

        tab = new JTable(table);
        tab.setRowSelectionAllowed(true);
        JScrollPane pane = new JScrollPane(tab);
        add(pane , BorderLayout.CENTER);
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton Pop = new JButton("View Popularity");
        JButton read = new JButton("Read");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        AddFromFile();
        //DONE
        tab.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                row = tab.rowAtPoint(e.getPoint());
                if (row >= 0) 
                {
                    tab.addRowSelectionInterval(row, row);
                    tab.setSelectionBackground(Color.GREEN);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) 
            {
                tab.clearSelection();
                tab.setSelectionBackground(tab.getSelectionBackground());
            }
        });
        //Done
        addBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BookAdd obj = new BookAdd(mainFrame.this);
                obj.setVisible(true);
            }
        });
        //to-do
        editBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                EditEntry obj = new EditEntry(mainFrame.this, table, row);
            }
        });
        //DONE
        deleteBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RemoveEntry obj = new RemoveEntry(mainFrame.this, table);
            }
        });
        //DONE
        Pop.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BarChart obj = new BarChart();
                obj.graph();
            }
        });
        
        read.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                read obj = new read();
            }
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(Pop);
        buttonPanel.add(read);
        add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().setBackground(Color.GRAY);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    //DONE
    public void addBook(String title , String author , String publication)
    {
        table.addRow(new Object[]{title , author , publication});
    }

    //DONE
    private void AddFromFile ()
    {
        try ( BufferedReader br= new BufferedReader(new FileReader("book.txt")))
        {
            String name , author , issue;
            int c;
            String line;
            while ( (line = br.readLine()) != null )
            {
                String [] p = line.split(",");
                if ( p.length == 4 )
                {
                    name = p[0].trim();
                    author = p[1].trim();
                    issue = p[2].trim();
                    c = Integer.parseInt(p[3].trim());
                    addBook(name, author, issue);
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

}

//DONE(100%)
class BookAdd extends JDialog 
{
    private JTextField title;
    private JTextField author;
    private JTextField publication;

    public BookAdd(JFrame parent) 
    {
        super(parent, "Add User", true);
        title = new JTextField(30);
        author = new JTextField(30);
        publication = new JTextField(4);

        JPanel mainP = new JPanel(new GridLayout(3 , 2));
        mainP.add(new JLabel("Title:"));
        mainP.add(title);
        mainP.add(new JLabel("Author:"));
        mainP.add(author);
        mainP.add(new JLabel("Publication"));
        mainP.add(publication);

        JButton addBtn = new JButton("Add!");
        addBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                String name = title.getText();
                String auth = author.getText();
                String publ = publication.getText();
                if (!name.isEmpty() && !auth.isEmpty() && !publ.isEmpty()) 
                {
                    mainFrame obj = (mainFrame) parent;
                    obj.addBook(name, auth, publ);
                    addInFile(name, auth, publ, 5);
                    JOptionPane.showMessageDialog(mainP, "Book Added.");
                    dispose();
                } 
                else 
                {
                    JOptionPane.showMessageDialog(BookAdd.this, "Cannot enter. Fill in all fields.");
                }
            }
        });
        add(addBtn, BorderLayout.SOUTH);
        add(mainP, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }
    //DONE
    private void addInFile (String bookName, String bookAuthor , String publication , int popCount)
    {
        Random rand = new Random();
        popCount = rand.nextInt(10);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("book.txt", true)))
        { 
            writer.newLine();
            writer.write(bookName + "," + bookAuthor + "," + publication + "," + popCount);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            System.err.println("Error writing data to the file.");
        }
    }

}

//DONE(100%)
class RemoveEntry extends JDialog 
{
    private JTextField entry;
    private JLabel entrytitle;

    public RemoveEntry(JFrame parent , DefaultTableModel tab)
    {
        super(parent, "Add User", true);
        entry = new JTextField(30);
        entrytitle = new JLabel("Enter the Title of Book to be removed");
        JPanel mainP = new JPanel(new GridLayout(1 , 2));
        mainP.add(entrytitle);
        mainP.add(entry);

        JButton removeBtn = new JButton("Remove!");
        removeBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boolean flag = false;
                String toRemove = entry.getText();
                for ( int i = 0 ; i < tab.getRowCount() ; i++ )
                {
                    String currentEntry = (String) tab.getValueAt(i, 0);
                    if (currentEntry.equals(toRemove)) 
                    {
                        flag = true;
                        tab.removeRow(i);
                        deleteFromFile(currentEntry);
                        return;
                    }
                }
                if ( flag == false )
                {
                    JOptionPane.showMessageDialog(RemoveEntry.this, "No entry found for this search");
                }
            }
        });
        add(mainP , BorderLayout.CENTER);
        add(removeBtn , BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    //DONE
    private void deleteFromFile( String toDelete )
    {
        String line;
        try
        {
            boolean flag = false;
            File inFile = new File("book.txt");
            File temFile = new File("temp.txt");

            BufferedReader read = new BufferedReader(new FileReader(inFile));
            BufferedWriter write = new BufferedWriter(new FileWriter(temFile));
            while ((line = read.readLine()) != null)
            {
                String [] p = line.split(",");
                if ( p[0].trim().equals(toDelete) )
                {
                    flag = true;
                }
                else
                {
                    write.write(line + System.lineSeparator());
                }
            }
            read.close();
            write.close();

            if (!flag)
            {
                JOptionPane.showMessageDialog(RemoveEntry.this, "No Entry Found.");
            }

            if ( inFile.delete())
            {
                if (temFile.renameTo(inFile))
                {
                    JOptionPane.showMessageDialog(RemoveEntry.this, "Entry Deleted Successfully.");
                }
                else
                {
                    JOptionPane.showMessageDialog(RemoveEntry.this, "Entry Not deleted.");
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}

//DONE(100%)
class EditEntry extends JDialog 
{
    private JTextField editBook;
    private JTextField editAuthor;
    private JTextField editPublication;

    public EditEntry(JFrame parent , DefaultTableModel tab , int r)
    {
        super(parent, "Add User", true);
        editBook = new JTextField(30);
        editAuthor = new JTextField(30);
        editPublication = new JTextField(4);

        JPanel EditPanel = new JPanel(new GridLayout(3 , 2));
        EditPanel.add(new JLabel("Edited Name:"));
        EditPanel.add(editBook);
        EditPanel.add(new JLabel("Edited Author:"));
        EditPanel.add(editAuthor);
        EditPanel.add(new JLabel("Edited Publication:"));
        EditPanel.add(editPublication);

        JButton editBtn = new JButton("Update!");
        editBtn.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String newName = editBook.getText() , newAuthor = editAuthor.getText() , newpublication = editPublication.getText();
                for ( int i = 0 ; i < tab.getRowCount() ; i++ )
                {
                    if ( i == r )
                    {
                        tab.setValueAt(newName, r,0);
                        tab.setValueAt(newAuthor, r,1);
                        tab.setValueAt(newpublication, r,2);
                        updateBookInFile(newName, newAuthor, newpublication, 0, r);
                        JOptionPane.showMessageDialog(EditEntry.this, "Entry Updates Successfully!");
                    }
                }
            }
        });

        add(EditPanel , BorderLayout.CENTER);
        add(editBtn , BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    private void updateBookInFile(String name , String auth , String issue , int count , int row)
    {
        Random rand = new Random();
        count = rand.nextInt(10);
        int counter = 0;
        String line;
        try 
        {
            BufferedReader read = new BufferedReader(new FileReader("book.txt"));
            BufferedWriter write = new BufferedWriter(new FileWriter("temp.txt"));
            while ( (line = read.readLine()) != null) 
            {
                if ( counter == row )
                {
                    System.out.println("Updating....");
                    write.write(name + "," + auth + "," + issue + "," + count);
                    write.newLine();
                }
                else
                {
                    write.write(line);
                    write.newLine();
                }
                counter++;
            }
            read.close();
            write.close();

            File orgFile = new File("book.txt");
            File tempFile = new File("temp.txt");
            tempFile.renameTo(orgFile);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

}

class read extends JFrame
{
    private JTextArea area;
    public read()
    {
        setTitle("Book Reading");
        setSize(500 , 400);
        area = new JTextArea();
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int op = JOptionPane.showConfirmDialog
                (read.this , "Do you want to exit" , "Exit Confirm" , JOptionPane.YES_NO_OPTION );
                if (op == JOptionPane.YES_OPTION)
                {
                    dispose();
                }
                else
                {
                    //do nothing
                }
            }
            
        });
        JScrollPane pane = new JScrollPane(area);

        add(pane , BorderLayout.CENTER);
        setVisible(true);
        displayFromFile();
    }

    private void displayFromFile()
    {
        String line;
        try 
        {
            BufferedReader br = new BufferedReader(new FileReader("readbook.txt"));
            area.setText("");
            while ((line = br.readLine()) != null)
            {
                area.append(line + "\n");
            }
            br.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}

//DONE(100%)
class BarChart extends JPanel {
    private DefaultCategoryDataset dataset;

    public DefaultPieDataset makeGraph() 
    {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        try (BufferedReader br = new BufferedReader(new FileReader("book.txt"))) 
        {
            String name, line;
            int count;
            while ((line = br.readLine()) != null) 
            {
                String[] p = line.split(",");
                if (p.length == 4) 
                {
                    name = p[0].trim();
                    count = Integer.parseInt(p[3].trim());
                    pieDataset.setValue(name, count);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return pieDataset;
    }
    public void graph() 
    {
        DefaultPieDataset dataset = makeGraph(); 
        JFreeChart chart = ChartFactory.createPieChart("Pie Chart", dataset, true, true, true);
        PiePlot plot = (PiePlot) chart.getPlot();
        ChartFrame frame = new ChartFrame("Pie Chart", chart);
        frame.setVisible(true);
        frame.setSize(450, 500);
    }


}


public class main
{
    public static void main ( String args[] )
    {
        mainFrame obj = new mainFrame();
    }
}