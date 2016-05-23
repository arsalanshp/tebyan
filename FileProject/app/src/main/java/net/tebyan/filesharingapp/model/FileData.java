package net.tebyan.filesharingapp.model;

/**
 * Created by F.piri on 1/24/2016.
 */
public class FileData {

    public String FileID;
    public String FolderID;
    public String AccountID;
    public String Title;
    public String Thumb;
    public String Size;
    public String Sharedate;
    public String Stared;
    public String FileTypeIcon;
    public String MonthStr;
    public String SharedBy;
    public boolean IsNewShare;
    public String FileContent;
    public boolean isHeader = false;
    public boolean Deleted;
    public boolean IsFolder;
    public boolean CandEdit;
    public int PublicStatus;

    public FileData(String title, boolean isHeader, boolean isFolder) {
        Title = title;
        this.isHeader = isHeader;
        this.IsFolder = isFolder;
    }

    public FileData() {
        isHeader = false;

    }
}
