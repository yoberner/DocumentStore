package edu.yu.cs.com1320.project.stage2;

import static org.junit.Assert.*;
import org.junit.*;

import edu.yu.cs.com1320.project.impl.StackImpl;

public class StackImplTest {

    @Test
    public void test1() {
        StackImpl<String> stack = new StackImpl<String>();
        
        String str1 = "this is string 1";
        String str2 = "this is string 2";
        String str3 = "this is string 3";
        String str4 = "this is string 4";

        stack.push(str1);
        assertEquals(str1, stack.pop());
        stack.push(str2);
        assertEquals(str2, stack.peek());
        stack.push(str3);
        assertEquals(str3, stack.peek());
        stack.push(str4);
        assertEquals(3, stack.size());
        assertEquals(str4, stack.pop());

        StackImpl<String> stack2 = new StackImpl<String>();
        assertEquals(null, stack2.pop());
        assertEquals(null, stack2.peek());
        assertEquals(0, stack2.size());
    }
}