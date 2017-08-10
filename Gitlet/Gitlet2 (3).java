package gitlet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.nio.file.StandardOpenOption;

/** A class for the commit objects.
  * @author Diana Akrami & Juan Cervantes
  */
public class Gitlet2 {

    Gitlet2() {
    }

    /** Initializes the gitlet repository. */
    void init() {
        File dir = new File(".gitlet");
        if (dir.exists()) {
            System.out.println("a gitlet version-control system already exists in the current directory.");
            return;
        } else {
            dir.mkdir();
            File next = new File(".gitlet/heads/master.ser");
            File currentBranch = new File(".gitlet/current");
            File heads = new File(".gitlet/heads");
            File currentname = new File(".gitlet/current/master.ser");
            File staged = new File(".gitlet/staged");
            File commits = new File(".gitlet/commits");
            File unstaged = new File(".gitlet/unstaged");
            File blobs = new File(".gitlet/blobs");
            try {
                heads.mkdir();
                staged.mkdir();
                commits.mkdir();
                unstaged.mkdir();
                currentBranch.mkdir();
                blobs.mkdir();
                Commit initial = new Commit("initial commit", null, null);
                FileOutputStream fileOut = new FileOutputStream(next);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(initial);
                Files.copy(next.toPath(), currentname.toPath());
            } catch (IOException e) {
                return;
                }
            }
        }
    /** Adds a file in the Working Directory into
        * the staging directory. */
    void add(String name) {
        File addedfile = new File(name);
        File next = new File(".gitlet/staged/" + name);
        File saverm = new File(".gitlet/unstaged/" + name);
        if (!addedfile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (saverm.exists()) {
            saverm.delete();
        }
        String hash = Utils.sha1(name, Utils.readContents(addedfile));
        if (getCurrentCommit().getBlobs() != null && getCurrentCommit().getBlobs().containsValue(hash)) {
            return;
        }
        Utils.writeContents(next, Utils.readContents(addedfile));
    }

    /** Fetches the current branch. */
    String getCurrentCommitBranch() {
        String branchh = null;
        File currentB = new File(".gitlet/current");
        for (File file: currentB.listFiles()) {
            branchh = file.getName();
        }
        return branchh;
    }
    /** Fetches the current Commit. */
    Commit getCurrentCommit() {
        String branchh = null;
        Commit prev = null;
        File currentB = new File(".gitlet/current");
        for (File file: currentB.listFiles()) {
            branchh = file.getName();
        }
        return deserializeCommit(".gitlet/heads/" +branchh);
        }
    /** Grabs the Commit specified in the parameters. */
    Commit getCommit(String hash) {
        Commit newCommit = null;
        File gettin = new File(".gitlet/commits/" + hash);
        if (gettin.exists()) {
            return deserializeCommit(".gitlet/commits/" + hash);
        } else {
        return deserializeCommit(".gitlet/commits/" + hash + ".ser");
        }
    }
    /** Updates the Branch with the Current Commit */
    void updateBranch(Commit newt) {
        String nextBranch = null;
        Commit theNext = null;
        File parent = new File(".gitlet/current");
        for (File file : parent.listFiles()) {
            nextBranch = file.getName();
        }
        try {
            File getHead = new File(".gitlet/heads/" + nextBranch);
            FileOutputStream fieOut = new FileOutputStream(getHead);
            ObjectOutputStream obetOut = new ObjectOutputStream(fieOut);
            obetOut.writeObject(newt);
        } catch (IOException e) {
        }
        try {
            File hideous = new File(".gitlet/current/" + nextBranch);
            FileOutputStream ieOut = new FileOutputStream(hideous);
            ObjectOutputStream betOut = new ObjectOutputStream(ieOut);
            betOut.writeObject(newt);
        } catch (IOException e) {
        }
    }

    /** Creates a new branch.*/
    void branch(String branchName) {
        File curr = new File(".gitlet/current");
        String path = null;
        File newBranch = new File(".gitlet/heads/" + branchName +".ser");
        for (File file: curr.listFiles()) {
            path = file.getName();
        }
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        } else {
            Commit currCom = deserializeCommit(".gitlet/current/" +path);
            try {
                FileOutputStream fieOut = new FileOutputStream(newBranch);
                ObjectOutputStream obetOut = new ObjectOutputStream(fieOut);
                obetOut.writeObject(currCom);
            } catch (IOException e) {
                System.out.println("p");
            }
        }
    }

    /** Deserializes the commit according to the path. */
    Commit deserializeCommit(String path) {
        Commit newCommit = null;
        File pathy = new File(path);
        try {
            FileInputStream f = new FileInputStream(pathy);
            ObjectInputStream objn = new ObjectInputStream(f);
            newCommit = (Commit) objn.readObject();
        } catch (IOException e) {
            String msg = "IOException while loading.";
            System.out.println(msg);
        } catch (ClassNotFoundException e) {
            String msg = "ClassNotFoundException while loading myCat.";
            System.out.println(msg);
        }
        return newCommit;

	}
    /** Deserializes a Blob. */
    Blob deserializeBlob(String path) {
        Blob newBlob = null;
        File pathy = new File(path);
        try {
            FileInputStream f = new FileInputStream(pathy);
            ObjectInputStream objn = new ObjectInputStream(f);
            newBlob = (Blob) objn.readObject();
        } catch (IOException e) {
            String msg = "IOException while loading.";
            return null;
        } catch (ClassNotFoundException e) {
            String msg = "ClassNotFoundException while loading myCat.";
            System.out.println(msg);
        }
        return newBlob;

    }
    /** Creates a new commit. */
    void commit(String message) {
        HashMap<String, String> blobs = new HashMap<String, String>();
        File staging = new File(".gitlet/staged");
        File unstage = new File(".gitlet/unstaged");
        File parent = new File(".gitlet/current");
        File workingD = new File(".");
        Commit newCommit = null;
        String par = getCurrentCommit().getHash();
        Commit currentCommit = getCurrentCommit();
        if (staging.listFiles().length == 0 && unstage.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        } else {
        for (File file : staging.listFiles()) {
            Blob next = new Blob(file.getName(), Utils.readContents(file));
            blobs.put(file.getName(), next.getHash());
            file.delete();
        }
        for (File file : unstage.listFiles()) {
            file.delete();
        }						
        for (File file : workingD.listFiles()) {
            if (getCurrentCommit().getMessage().compareTo("initial commit") == 0) {
            break;
        }
        if ((currentCommit.getBlobs().get(file.getName()) != null) && (blobs.get(file.getName()) == null)) {
            File unstaged = new File(".gitlet/unstaged/" + file.getName());
                if (unstaged.exists()) {
                    unstaged.delete();
                    continue;
                } else {
                    blobs.put(file.getName(), currentCommit.getBlobs().get(file.getName()));
                }
                }
        }
        newCommit = new Commit(message, par, blobs);
        updateBranch(newCommit);
        }
    }

    void read(){
        File commit = new File (".gitlet/commits");
        for (File file : commit.listFiles()) {
            System.out.println("hi");
            Commit neew = deserializeCommit(".gitlet/commits/" + file.getName());
            if (neew.getBlobs() == null) {
                continue;
            } else {
                for (String name: neew.getBlobs().keySet()) {
                System.out.println(name);
                }
            }
        }
    }

    boolean blobCheckerGiven(HashMap<String, String> givenBlobs, HashMap<String,String> currBlobs, HashMap<String,String> splitBlobs) {
        boolean conflict = false;
        for (String name : givenBlobs.keySet()) {
            File checkout = new File(name);
            File nowStage = new File(".gitlet/staged/" + name);
            if (currBlobs.get(name) != null && splitBlobs.get(name) != null 
                && currBlobs.get(name).equals(splitBlobs.get(name)) &&
                    !givenBlobs.get(name).equals(splitBlobs.get(name))) {
                Blob next = deserializeBlob(".gitlet/blobs/" + givenBlobs.get(name) + ".ser");
                Utils.writeContents(checkout, next.getFileContents());
                Utils.writeContents(nowStage, next.getFileContents());
            }
            if (splitBlobs.get(name) == null && currBlobs.get(name) == null) {
                Blob next = deserializeBlob(".gitlet/blobs/" + givenBlobs.get(name) + ".ser");
                Utils.writeContents(checkout, next.getFileContents());
                Utils.writeContents(nowStage, next.getFileContents());
            }
            /** Curr is null, Split is not null, Split is different from given */
            if (currBlobs.get(name) == null && splitBlobs.get(name) != null &&
                !givenBlobs.get(name).equals(splitBlobs.get(name))) {
                conflict = true;

            }
             /** Curr is not null, Split is null, given is different from curr */
            if (currBlobs.get(name) != null && splitBlobs.get(name) == null &&
                !givenBlobs.get(name).equals(currBlobs.get(name))) {
                conflict = true;

            }
             /** Curr is not null, Split is not null, given is different from curr
             given is different from split, current is different from split */
            if (currBlobs.get(name) != null && splitBlobs.get(name) != null &&
                !givenBlobs.get(name).equals(splitBlobs.get(name)) &&
                !currBlobs.get(name).equals(splitBlobs.get(name))) {
                    conflict = true;
                }

            // if (currBlobs.get(name) != null && splitBlobs.get(name) != null &&
            //     !currBlobs.get(name).equals(splitBlobs.get(name)) &&
            //         !givenBlobs.get(name).equals(splitBlobs.get(name))  || 
            //             currBlobs.get(name) == null ||
              
            if (conflict == true) {
                    Blob currblob = deserializeBlob(".gitlet/blobs/" + currBlobs.get(name) + ".ser");
                    Blob givenblob = deserializeBlob(".gitlet/blobs/" + givenBlobs.get(name) + ".ser");
                    String equal = "=======\n";
                    String head  = "<<<<<<< HEAD\n";
                    String arrow = ">>>>>>>";
                try {

                    Files.write(checkout.toPath(), head.getBytes());
                    if (currblob != null) {
                        Files.write(checkout.toPath(), currblob.getFileContents(), StandardOpenOption.APPEND);
                    }
                    Files.write(checkout.toPath(), equal.getBytes(),  StandardOpenOption.APPEND);
                    Files.write(checkout.toPath(), givenblob.getFileContents(), StandardOpenOption.APPEND);
                    Files.write(checkout.toPath(), arrow.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.out.println("didn't work");
                }
                conflict = true;
            }

        }
        return conflict;
    }

    boolean blobCheckerCurrent(HashMap<String, String> currBlobs, HashMap<String,String> givenBlobs, HashMap<String,String> splitBlobs) {
        boolean conflict = false;
        for (String name : currBlobs.keySet()) {
            File checkoutt = new File(name);
            File nowStagee = new File(".gitlet/staged/" + name);
            if (splitBlobs.get(name) != null && splitBlobs.get(name).equals(currBlobs.get(name))
                && givenBlobs.get(name) == null) {
                checkoutt.delete();
            }


            /** Curr is null, Split is not null, Split is different from given */
            if (givenBlobs.get(name) == null && splitBlobs.get(name) != null &&
                !currBlobs.get(name).equals(splitBlobs.get(name))) {
                conflict = true;

            }
             /** Curr is not null, Split is null, given is different from curr */
            if (givenBlobs.get(name) != null && splitBlobs.get(name) == null &&
                !currBlobs.get(name).equals(givenBlobs.get(name))) {
                conflict = true;

            }
             /** Curr is not null, Split is not null, given is different from curr
             given is different from split, current is different from split */
            if (givenBlobs.get(name) != null && splitBlobs.get(name) != null &&
                !givenBlobs.get(name).equals(splitBlobs.get(name)) &&
                !currBlobs.get(name).equals(splitBlobs.get(name))) {
                    conflict = true;
                }

            if (conflict) {
                Blob currblob = deserializeBlob(".gitlet/blobs/" + currBlobs.get(name) + ".ser");
                Blob givenblob = deserializeBlob(".gitlet/blobs/" + givenBlobs.get(name) + ".ser");
                String equal = "=======\n";
                String head  = "<<<<<<< HEAD\n";
                String arrow = ">>>>>>>";
                try {
                    Files.write(checkoutt.toPath(), head.getBytes());
                    Files.write(checkoutt.toPath(), currblob.getFileContents(), StandardOpenOption.APPEND);
                    Files.write(checkoutt.toPath(), equal.getBytes(),  StandardOpenOption.APPEND);
                    if (givenblob != null) {
                        Files.write(checkoutt.toPath(), givenblob.getFileContents(), StandardOpenOption.APPEND);
                    }
                    Files.write(checkoutt.toPath(), arrow.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.out.println("didn't work");
                }
            conflict = true;
            }
        }
        return conflict;
    }
    ArrayList<String> parentIDS(Commit commit) {
        ArrayList<String> parents = new ArrayList<String>();
        while(commit.getParent() != null) {
            parents.add(commit.getParent());
            commit = deserializeCommit(".gitlet/commits/" + commit.getParent() + ".ser");
        }
        return parents;
    }

    Commit findSplit(ArrayList<String> parents, Commit splitCommit) {
        while (splitCommit.getParent() != null) {
        if (parents.contains(splitCommit.getHash())) {
            return splitCommit;
        }
        splitCommit = deserializeCommit(".gitlet/commits/" + splitCommit.getParent() + ".ser");
        }
        return splitCommit;
    }

    void merge(String givenBranch) {
        File branch = new File(".gitlet/heads/" + givenBranch + ".ser");
        File wd = new File(".");
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exists.");
            return;
        }
        if (givenBranch.equals(getCurrentCommitBranch())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        } 
        checksStage();
        checksUnstage();
        Commit curr = getCurrentCommit();
        for (File file : wd.listFiles()) {
            if (!file.isFile() || file.getName().contains(".g") || file.getName().contains(".D") || file.getName().contains("Make")) {
                continue;
            }
            if (curr.getBlobs().get(file.getName()) == null) {
                System.out.println("There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }
        String currHash = curr.getHash();
        Commit parent = deserializeCommit(".gitlet/commits/" + curr.getParent() + ".ser");
        Commit givenCommit = deserializeCommit(".gitlet/heads/" + givenBranch +".ser");
        String givenHash = givenCommit.getHash();
        Commit addHistory = getCurrentCommit();
        ArrayList<String> currHistory = parentIDS(getCurrentCommit());
        Commit splitCommit = deserializeCommit(".gitlet/heads/" + givenBranch +".ser");
        splitCommit = findSplit(currHistory, splitCommit);
        String split = splitCommit.getHash();
        if (givenHash.equals(split)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return; 
        }
        if (split.equals(currHash)) {
            updateBranch(givenCommit);
            System.out.println("Current branch fast-forwarded.. ");
            return;
        }
        HashMap<String, String> currBlobs = curr.getBlobs();
        HashMap<String, String> splitBlobs = splitCommit.getBlobs();
        HashMap<String, String> givenBlobs = givenCommit.getBlobs();
        boolean conflict1 = blobCheckerCurrent(currBlobs, givenBlobs, splitBlobs);
        boolean conflict2 = blobCheckerGiven(givenBlobs, currBlobs, splitBlobs); 
        printMergeConflict(conflict2, conflict1, givenBranch);
    }

    void printMergeConflict(boolean conflictt, boolean conflict2, String givenBranch) {
        if (conflictt || conflict2) {
            System.out.println("Encountered a merge conflict.");
        } else {
            int period = getCurrentCommitBranch().indexOf(".");
            commit("Merged " + getCurrentCommitBranch().substring(0, period) + " with " + givenBranch +".");
        }
    }

    void checksStage() {
        File stage = new File(".gitlet/staged");
        if (stage.listFiles().length > 0) {
            System.out.println("You have uncommited changes.");
        }
    }

    void checksUnstage() {
        File stage = new File(".gitlet/unstaged");
        if (stage.listFiles().length > 0) {
            System.out.println("You have uncommited changes.");
        }
    }

	/** untracks a file */
    void rm(String name) {
        File lostFile = new File(".gitlet/staged/" + name);
        File saverm = new File(".gitlet/unstaged/" + name);
        Commit parents = getCurrentCommit();
        int i = 1;
        if (parents.getMessage().compareTo("initial commit") == 0 && !lostFile.exists()) {
            System.out.println("No reason to remove the file.");
            return;
        }
        if (lostFile.exists()) {
            File deletedFilee = new File(name);			
            if (parents.getBlobs() != null && parents.getBlobs().get(name) != null) {
                Utils.writeContents(saverm, Utils.readContents(deletedFilee));
                lostFile.delete();
            }
            lostFile.delete();
            i+=1;
        }
        if (parents.getMessage().compareTo("initial commit") != 0 && parents.getBlobs().get(name) != null) {
            File deletedFile = new File(name);
            if (!deletedFile.exists()) {
                Blob deletedblob = deserializeBlob(".gitlet/blobs/" + parents.getBlobs().get(name) + ".ser");
                Utils.writeContents(saverm, deletedblob.getFileContents());
                return;
            }
            Utils.writeContents(saverm, Utils.readContents(deletedFile));
            deletedFile.delete();
            i+=1;
        } 
        if (i == 1) {
            System.out.println("No reason to remove the file.");
        }
    }

    void deleteBranch(String head) {
        File branch = new File (".gitlet/heads/" + head +".ser");
        File curr = new File (".gitlet/current/" + head +".ser");
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.A branch with that name does not exist.");
        }
        if (curr.exists()) {
            System.out.println("Cannot remove the current branch.");
        }
        branch.delete();
        }

    void log() {
        Commit thelog = getCurrentCommit();
        Commit parent = null;
        int i = 1;
        if (thelog.getMessage().compareTo("initial commit") == 0) {
            i += 1;
        } else {
            parent = getCommit(thelog.getParent());
        }
        if (thelog.getMessage().compareTo("initial commit") == 0) {
            return;
        }
        while (thelog.getMessage().compareTo("initial commit") != 0) {
            System.out.println("===");
            System.out.println("Commit " + thelog.getHash());
            thelog.getDate();
            System.out.println(thelog.getMessage() + "");
            System.out.println("");
            if(i > 1) {
                return;
            }
            thelog = parent;
            if (parent.getParent() == null) {
                break;
            }
            parent = getCommit(thelog.getParent());
        }
        if (thelog.getMessage().compareTo("initial commit") == 0) {
            System.out.println("===");
            System.out.println("Commit " + thelog.getHash());
            thelog.getDate();
            System.out.println(thelog.getMessage());
            System.out.println("");
            return;
        }
    }

    void globalLog() {
        Commit nee = null;
        File yo = new File(".gitlet/commits");
        for (File file : yo.listFiles()) {
            getCommit(file.getName()).getLog();
        }
    }

    void find(String mes) {
        Commit lol = null;
        int i = 1;
        File yoy = new File(".gitlet/commits");
        for (File file : yoy.listFiles()) {
            if (getCommit(file.getName()).getMessage().equals(mes)) {
                System.out.println(getCommit(file.getName()).getHash());
                i+=1;
            }
        }
        if (i < 2) {
            System.out.println("Found no commit with that message.");
        }
    }

    void status() {
        System.out.println("=== Branches ===");
        File current = new File (".gitlet/current");
        File staged = new File (".gitlet/staged");
        File branches = new File (".gitlet/heads");
        File unstaged = new File (".gitlet/unstaged");
        Commit curCommit = getCurrentCommit();
        String curbranch = null;
        for (File file: current.listFiles()) {
            curbranch = file.getName();
        }
        for (String name : Utils.plainFilenamesIn(branches)) {
            int period = name.indexOf(".");
            if (name.compareTo(curbranch) == 0) {
                System.out.println("*" + name.substring(0, period));
            } else {
            System.out.println(name.substring(0, period));
            }
        }
        System.out.println("");
        System.out.println("=== Staged Files ===");
        for (String name : Utils.plainFilenamesIn(staged)) {
            System.out.println(name);
        }
        System.out.println("");
        System.out.println("=== Removed Files ===");
        for (String name : Utils.plainFilenamesIn(unstaged)) {
            System.out.println(name);
        }
        System.out.println("");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");
        System.out.println("=== Untracked Files ===");
        System.out.println("");
    }

    void fileCheckout(String commitPath, String fileName) {
        File staging = new File(".gitlet/staged");
        File dir = new File(".");
        Commit curr = null;
        if (commitPath == null) {
            curr = getCurrentCommit();
        } else {
            curr = deserializeCommit(commitPath);
        }
        if (fileName == null) {
            for (File file : dir.listFiles()) {
                String name = file.getName();
                if (curr.getBlobs() != null && curr.getBlobs().get(name) != null) {
                    file.delete();
                    File next = new File(name);
                    Blob blob = deserializeBlob(".gitlet/blobs/" + curr.getBlobs().get(name) + ".ser");
                    Utils.writeContents(next, blob.getFileContents());
                }
                if (curr.getBlobs() != null && curr.getBlobs().get(name) == null) {
                    file.delete();
                }
            }
            for (File file : staging.listFiles()) {
                file.delete();
            }
            for (String hash : curr.getBlobs().values()) {
                Blob that = deserializeBlob(".gitlet/blobs/" + hash + ".ser");
                String name = that.getName();
                File create = new File(name);
                Utils.writeContents(create, that.getFileContents());
            }
            updateBranch(curr);
            return;
        }
        File getFile = new File(fileName);
        if (curr.getMessage().compareTo("initial commit") == 0) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        if (curr.getBlobs() != null) {
            for (String name : curr.getBlobs().keySet()) {
                if (name.compareTo(fileName) == 0) {
                    Blob blob = deserializeBlob(".gitlet/blobs/" + curr.getBlobs().get(fileName) + ".ser");
                    Utils.writeContents(getFile, blob.getFileContents());
                    return;
                }
            }
            System.out.println("File does not exist in that commit.");
        }
    }

    void fileIDCheckout(String commitID, String fileName) {
        File commitD = new File(".gitlet/commits");
        for (File file : commitD.listFiles()) {
            if (file.getName() == commitID + ".ser" || file.getName().contains(commitID)) {
                fileCheckout((".gitlet/commits/" + file.getName()), fileName);
                return;
            }
        }
        System.out.println("No commit with that id exists.");
        return;
    }

    void reset(String commitID) {
        fileIDCheckout(commitID, null);
    }

    void branchCheckout(String branchName) {
        File branch = new File(".gitlet/heads/" + branchName + ".ser");
        File nocurr = new File(".gitlet/current/" + branchName + ".ser");
        File stage = new File(".gitlet/staged");
        File wd = new File(".");
        for (File file : wd.listFiles()) {
            if (!file.isFile()) {
                continue;
            }
            if (file.getName().contains(".gitignore") || file.getName().contains("Make")
                || file.getName().contains(".DS")) {
                continue;
            }
            File check = new File(".gitlet/blobs/" + Utils.sha1(file.getName(), Utils.readContents(file)) + ".ser");
            if (!check.exists()) {
                System.out.println("There is an untracked file in the way; delete it or add it first.");
                return;
            }
        }
        if (nocurr.exists()) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        Commit curCommit = getCurrentCommit();
        if (curCommit.getBlobs() != null) {
            for (String name : getCurrentCommit().getBlobs().keySet()) {
                File checker = new File(name);
                if (checker.exists()) {
                    checker.delete();
                }
            }
        }
        File curr = new File(".gitlet/current/" + branchName + ".ser");
        File nomore = new File(".gitlet/current/" + getCurrentCommitBranch());
        Commit next = deserializeCommit(".gitlet/heads/" + branchName + ".ser");
        try {
            FileOutputStream fileOut = new FileOutputStream(curr);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(next);
            nomore.delete();
        } catch (IOException e) {
            System.out.println("didnt work");
        }
        if (next.getBlobs() != null) {
            for (String names : next.getBlobs().keySet()) {
                File make = new File (names);
                String hash = next.getBlobs().get(names);
                Blob blob = deserializeBlob(".gitlet/blobs/" + hash + ".ser");
                Utils.writeContents(make, blob.getFileContents());
            }
        }
        for (File file : stage.listFiles()) {
            file.delete();
        }	
    }
}
