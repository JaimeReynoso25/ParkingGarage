module SeniorProject {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.graphics;
	requires org.slf4j;
	requires javafx.base;
	requires org.junit.jupiter.api; // JUnit 5 API
	requires org.mockito; // For Mockito
	requires net.bytebuddy;
	
//	opens application to javafx.graphics, javafx.fxml;
	
	
	    
	
	
	 // Export packages that need to be accessible by FXML and other modules
    opens registerpkg to javafx.fxml;
    opens loginpkg to javafx.fxml;
    opens addfundspkg to javafx.fxml;
    opens menupkg to javafx.fxml;
    opens reservespotpkg to javafx.fxml;
    opens wheresmycarpkg to javafx.fxml;
    exports application;
}
