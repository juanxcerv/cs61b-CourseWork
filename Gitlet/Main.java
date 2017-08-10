package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        Gitlet2 newGit = new Gitlet2();
        String command = args[0];
        switch(command) {
            case "read":
                newGit.read();
                return;
            case "merge":
                newGit.merge(args[1]);
                return;
            case "init":
                newGit.init();
                return;
            case "add":
                newGit.add(args[1]);
                return;
              case "global-log":
                newGit.globalLog();
                return;
            case "commit":
                if (args.length < 2 || args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    return;
                } else {
                    newGit.commit(args[1]);
                    return;
                }
            case "rm":
                newGit.rm(args[1]);
                return;
            case "log":
                newGit.log();
                return;
            case "find":
                newGit.find(args[1]);
                return;
            case "status":
                newGit.status();
                return;
            case "checkout":
                if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    newGit.fileCheckout(null, args[2]);
                    return;
                }
                if (args.length == 4) {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                        return;
                    }
                    newGit.fileIDCheckout(args[1], args[3]);
                    return;
                }
                if (args.length == 2) {
                    newGit.branchCheckout(args[1]);
                    return;
                }
                return;
            case "branch":
                newGit.branch(args[1]);
                return;
            case "reset": 
                newGit.reset(args[1]);
                return;
            case "rm-branch": 
                newGit.deleteBranch(args[1]);
                return;
            }
        System.out.println("No command with that name exists.");
    }

    

}
