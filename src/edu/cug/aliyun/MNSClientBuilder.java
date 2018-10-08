package edu.cug.aliyun;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.utils.ServiceSettings;

public class MNSClientBuilder {
    private static MNSClient mnsClient;

    private MNSClientBuilder(){
        CloudAccount account = new CloudAccount(
                ServiceSettings.getMNSAccessKeyId(),
                ServiceSettings.getMNSAccessKeySecret(),
                ServiceSettings.getMNSAccountEndpoint());
        mnsClient = account.getMNSClient(); //this client need only initialize once
    }

    public static MNSClient getInstance(){
            MNSClientBuilder mnsClientBuilder = new MNSClientBuilder();
            return mnsClientBuilder.mnsClient;
    }
}
