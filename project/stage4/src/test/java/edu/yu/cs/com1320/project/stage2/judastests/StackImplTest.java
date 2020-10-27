package edu.yu.cs.com1320.project.stage2.judastests;

//import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class StackImplTest {
    private StackImpl<Undoable> stack;
    private GenericCommand<URI> cmd1;
    private GenericCommand<URI> cmd2;

    @Before
    public void initVariables() throws URISyntaxException {
        this.stack = new StackImpl<Undoable>();
        //uri & cmd 1
        URI uri1 = new URI("http://www.test1.net");
        this.cmd1 = new GenericCommand<URI>(uri1, target -> {
            return target.equals(uri1);
        });
        //uri & cmd 2
        URI uri2 = new URI("http://www.test2.net");
        this.cmd2 = new GenericCommand<URI>(uri2, target -> {
            return target.equals(uri2);
        });
        this.stack.push(this.cmd1);
        this.stack.push(this.cmd2);
    }

    @Test
    public void pushAndPopTest(){
        GenericCommand<URI> pcmd = (GenericCommand<URI>) stack.pop();
        assertEquals("first pop should've returned second command",this.cmd2,pcmd);
        pcmd = (GenericCommand<URI>) stack.pop();
        assertEquals("second pop should've returned first command",this.cmd1,pcmd);
    }

    @Test
    public void peekTest(){
        GenericCommand<URI> pcmd = (GenericCommand<URI>) this.stack.peek();
        assertEquals("first peek should've returned second command",this.cmd2,pcmd);
        pcmd = (GenericCommand<URI>) this.stack.pop();
        assertEquals("first pop should've returned second command",this.cmd2,pcmd);

        pcmd = (GenericCommand<URI>) this.stack.peek();
        assertEquals("second peek should've returned first command",this.cmd1,pcmd);
        pcmd = (GenericCommand<URI>) this.stack.pop();
        assertEquals("second pop should've returned first command",this.cmd1,pcmd);
    }
    @Test
    public void sizeTest(){
        assertEquals("two commands should be on the stack",2,this.stack.size());
        this.stack.peek();
        assertEquals("peek should not have affected the size of the stack",2,this.stack.size());
        this.stack.pop();
        assertEquals("one command should be on the stack after one pop",1,this.stack.size());
        this.stack.peek();
        assertEquals("peek still should not have affected the size of the stack",1,this.stack.size());
        this.stack.pop();
        assertEquals("stack should be empty after 2 pops",0,this.stack.size());
    }
}