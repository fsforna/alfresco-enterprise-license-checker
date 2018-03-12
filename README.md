# Alfresco Enterprise - License Checker
A simple Alfresco license checker tool.

The program aims to help alfresco administrators to automatically monitor license status. 

If validity period of the license is near to warning range, an email will be sent to pre-configued addresses. 
	
# How to build it yourself
Clone the project and configure the checker by editing the `conf.properties` file. Then run `mvn package`.

To execute the program you can use i.e:

	nohup /opt/alfresco/java/bin/java -cp  "alfresco-enterprise-license-checker-1.0.jar:$(find lib/ -name "*.jar" | sed -e 's,^,:,g' | tr -d '\n')" LicenseChecker > result.log 2>&1 &
    
# Author
Francesco Fornasari
