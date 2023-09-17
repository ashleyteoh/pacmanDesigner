package src.mapeditor.editor;

import java.io.File;
import java.util.*;

public class GameCheckerAdapter implements CheckerAdapter {
    MapCallback log = new MapCallback();
    File file;

    public GameCheckerAdapter(File file) {
        this.file = file;
    }

    @Override
    public boolean checkFile() {
//        MapCallback log = new MapCallback();
//        boolean fail = false;
//        checkFileNameFormat(log, file);
//        checkFileNameDup(log, file);

        return (checkFileNameFormat(log, file) && checkFileNameDup(log, file));
    }

    public static boolean checkFileNameFormat(MapCallback log, File directory){
        File[] files = directory.listFiles();
        boolean validFormat = false;
        for(File f : files) {
            String filename = f.getName();
            if (filename.endsWith(".xml") && Character.isDigit(filename.charAt(0))) {
                validFormat = true;
            }
        }

        if (!validFormat) {
            log.dirInvalid(directory.getName());
        }
        return validFormat;
    }

    public static boolean checkFileNameDup(MapCallback log, File directory) {
        HashMap<Integer, Integer> nameCount = new HashMap<Integer, Integer>();
        HashMap<Integer, ArrayList<String>> nameString = new HashMap<>();
        boolean status = true;

        File[] files = directory.listFiles();

        for(File f : files) {
            String filename = f.getName();
            List<String> tokens = Arrays.stream(filename.split("\\D+")).filter(s -> s.length() > 0).toList();
            if (tokens.size() == 0) continue;
            int fileNum = Integer.parseInt(tokens.get(0));
            if (nameCount.containsKey(fileNum)) {
                nameCount.put(fileNum, nameCount.get(fileNum) + 1);
                ArrayList<String> dupNames = nameString.get(fileNum);
                dupNames.add(f.getName());
                nameString.put(fileNum, dupNames);
            }
            else {
                nameCount.put(fileNum, 1);
                ArrayList<String> dupNames = new ArrayList<>();
                dupNames.add(f.getName());
                nameString.put(fileNum, dupNames);
            }
        }

        for (Map.Entry<Integer, Integer> set : nameCount.entrySet()) {
            if (set.getValue() > 1) {
                status = false;
                log.dupNames(directory.getName(), nameString.get(set.getKey()));
            }
        }


        return status;
    }

}
