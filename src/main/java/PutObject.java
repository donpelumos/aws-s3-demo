/**
 * Created by Pelumi on 26-Feb-2021
 */

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * Upload a file to an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class PutObject {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "To run this example, supply the name of an S3 bucket and a file to\n" +
                "upload to it.\n" +
                "\n" +
                "Ex: PutObject <bucketname> <filename>\n";
        String fileName = "C:\\Users\\Owner\\Desktop\\heroku.png";
        String bucketName = "pelumi-demo";
        File file = new File(fileName);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build();


        System.out.println("Done!");
    }
    
    private static void listObjects(AmazonS3 s3, String bucketName, String keyName){
        try {
            // get the current ACL
            AccessControlList acl = s3.getObjectAcl(bucketName, keyName);
            // set access for the grantee
            //EmailAddressGrantee grantee = new EmailAddressGrantee("poyefeso@gmail.com");
            GroupGrantee grantee = GroupGrantee.AllUsers;
            Permission permission = Permission.FullControl;
            acl.grantPermission(grantee, permission);
            s3.setObjectAcl(bucketName, keyName, acl);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        System.out.format("Objects in S3 bucket %s:\n", bucketName);
        ListObjectsV2Result result2 = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result2.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }
    
    private static void uploadObject(AmazonS3 s3, String bucketName, String keyName, File file){
        PutObjectResult result = null;

        System.out.format("Uploading %s to S3 bucket %s...\n", file.getAbsolutePath(), bucketName);
        
        try {
            result = s3.putObject(bucketName, keyName, file);
            if(result != null){

            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}

