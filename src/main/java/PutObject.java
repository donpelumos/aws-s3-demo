/**
 * Created by Pelumi on 26-Feb-2021
 */

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        System.out.println("******************************************************");
        listObjects(s3, bucketName);
        uploadObject(s3, bucketName, file.getName(), file);
        listObjects(s3, bucketName);
        System.out.println("******************************************************");
        createFolder(s3, bucketName, "demo-folder");
        uploadObject(s3, bucketName, file.getName(), "demo-folder",  file);
        listObjects(s3, bucketName);
        System.out.println("******************************************************");
        deleteObject(s3, bucketName, "demo-folder/heroku.png");
        listObjects(s3, bucketName);

        s3.shutdown();
        System.out.println("Done!");
    }
    
    private static void listObjects(AmazonS3 s3, String bucketName){

        System.out.format("Objects in S3 bucket %s:\n", bucketName);
        ListObjectsV2Result result2 = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result2.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }
    public static List<S3ObjectSummary> getListObjects(AmazonS3 s3, String bucketName){

        System.out.format("Objects in S3 bucket %s:\n", bucketName);
        ListObjectsV2Result result2 = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result2.getObjectSummaries();
        return objects;
    }
    
    private static void uploadObject(AmazonS3 s3, String bucketName, String keyName, File file){
        PutObjectResult result;

        System.out.format("Uploading %s to S3 bucket %s...\n", file.getAbsolutePath(), bucketName);
        
        try {
            result = s3.putObject(bucketName, keyName, file);
            if(result != null){

            }
        }
        catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
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
    }

    private static void uploadObject(AmazonS3 s3, String bucketName, String keyName, String folderName, File file){
        if(!folderExists(folderName, s3, bucketName)){
            System.out.println("Folder does not exist");
        }
        else{
            System.out.println("Folder exists");
            keyName = folderName+"/"+keyName;
            uploadObject(s3, bucketName, keyName, file);
        }
    }

    public static boolean folderExists(String folderName, AmazonS3 s3, String bucketName){
        String fullDescFileName = folderName+"/"+folderName+"-desc.txt";
        List<S3ObjectSummary> bucketObjects = getListObjects(s3, bucketName);
        for (S3ObjectSummary os : bucketObjects) {
            if(os.getKey().equalsIgnoreCase(fullDescFileName)){
                return true;
            }
        }
        return false;
    }

    public static void createFolder(AmazonS3 s3, String bucketName, String folderName){
        File file = createDescFile(folderName);
        if(file != null) {
            writeToDescFile(file);
        }
        String keyName = folderName+"/"+file.getName();
        uploadObject(s3, bucketName, keyName, file);
    }

    public static File createDescFile(String fileName){
        try {
            File file = new File(fileName+"-desc.txt");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            return file;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }
    }

    public static void writeToDescFile(File file){
        String descText = "Descriptor Text";
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(descText);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void deleteObject(AmazonS3 s3, String bucketName, String keyName){
        try {
            s3.deleteObject(bucketName, keyName);
        }
        catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}

