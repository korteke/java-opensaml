
package org.opensaml.util.net;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IPRangeTest {

    private void testInvalid(final String address) {
        try {
            IPRange.parseCIDRBlock(address);
            Assert.fail("address should have been invalid: " + address);
        } catch (IllegalArgumentException e) {
            // expected behaviour
            return;
        }
    }
    
    @Test
    public void validV4Addresses() {
        IPRange.parseCIDRBlock("1.2.3.4/32");
        IPRange.parseCIDRBlock("0.0.0.0/8");
        IPRange.parseCIDRBlock("0.0.0.0/0");
    }
    
    @Test
    public void validV6Addresses() {
        IPRange.parseCIDRBlock("0:0:0:0:0:0:0:0/128");
        IPRange.parseCIDRBlock("0:0:0:0:0:0:0:0/0");
        IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/128");
        IPRange.parseCIDRBlock("1234:5678::BBBB:CCCC/128");
        IPRange.parseCIDRBlock("2001:630:200::/48");
        IPRange.parseCIDRBlock("::0BAD:7/128");
    }
    
    @Test
    public void invalidJunkAddresses() {
        testInvalid("/32");
        testInvalid("f/32");
    }
    
    @Test
    public void invalidV4Addresses() {
        testInvalid("1/32");
        testInvalid("1.2/32");
        testInvalid("1.2.3/32");
        testInvalid("1.2.3.4/33");
        testInvalid("1.2.3.4/-3");
        testInvalid("1.2.3.4/wrong");
    }
    
    @Test
    public void invalidV6Addresses() {
        testInvalid("0:0/128");
        testInvalid("1:2:3:4:5:6:7/128");
        testInvalid("::0BAD::7/128");
        testInvalid("1:2:3:4:5:6:7:8/-5");
        testInvalid("1:2:3:4:5:6:7:8/129");
        testInvalid("1:2:3:4:5:6:7:8/wrong");
    }

    @Test
    public void contains() {
        // IPRange given a network address
        IPRange networkRange = IPRange.parseCIDRBlock("192.168.117.192/28");
        
        // IPRange given a host address
        IPRange hostRange = IPRange.parseCIDRBlock("192.168.117.199/28");
        
        // test for contain
        byte[] bytes = new byte[]{(byte)192, (byte)168, 117, (byte)191};
        Assert.assertFalse(networkRange.contains(bytes));
        Assert.assertFalse(hostRange.contains(bytes));
        
        for (int host = 0; host < 16; host++) {
            bytes[3] = (byte)(192+host);
            Assert.assertTrue(networkRange.contains(bytes));
            Assert.assertTrue(hostRange.contains(bytes));
        }
        
        bytes[3] = (byte)(192+16);
        Assert.assertFalse(networkRange.contains(bytes));
        Assert.assertFalse(hostRange.contains(bytes));
    }
    
}
