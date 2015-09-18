package net.codjo.test.release.util.ssh;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.codjo.test.release.util.ssh.SshTestConstants.SAMChecker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.codjo.test.release.util.ssh.SecureCommand.DEFAULT_SSH_PORT;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_HOST;
import static net.codjo.test.release.util.ssh.SshTestConstants.SAM_LOGIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
/**
 *
 */
@RunWith(JunitExtRunner.class)
public class UnixSessionFactoryTest {

    @Test
    @RunIf(value = SAMChecker.class)
    public void test_connect_to_wd_sam() throws Exception {
        UnixSessionFactory unixSessionFactory = new UnixSessionFactory(SAM_LOGIN, SAM_HOST, DEFAULT_SSH_PORT);

        Session session = unixSessionFactory.createSession();

        session.connect();
        session.disconnect();
    }


    @Test
    public void test_cant_connect_with_bad_port() throws Exception {
        UnixSessionFactory unixSessionFactory = new UnixSessionFactory(SAM_LOGIN, SAM_HOST, 1111);

        Session session = unixSessionFactory.createSession();

        try {
            session.connect();
            fail("Connection should not be possible on port 1111");
        }
        catch (JSchException e) {
            assertEquals("java.net.ConnectException: Connection refused: connect", e.getLocalizedMessage());
        }
    }

/*
    @Test
//    This test has been desactived because it locks the account (even with a successfull test before)
    public void test_connect_to_wd_sam_wrong_key() throws Exception {
        unixSessionFactory = new UnixSessionFactory(SAM_LOGIN,
                                                    SAM_HOST,
                                                    getClass().getResource("ssh_test_key.txt"));

        Session session = unixSessionFactory.createSession();
        try {
            session.connect();
            fail();
        }
        catch (JSchException e) {
            assertEquals("Auth fail", e.getMessage());
        }
    }
*/
}
