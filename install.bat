
call mvn install:install-file -Dfile=.\lib\log4j.jar -DgroupId=org.apache.log4j -DartifactId=org-apache-log4j -Dversion=1.2.16 -Dpackaging=jar
call mvn install:install-file -Dfile=.\lib\net.java.hulp.i18n.jar -DgroupId=net.java.hulp.i18n -DartifactId=net.java.hulp.i18n -Dversion=2.1-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=.\lib\index-core.jar -DgroupId=oracle.hsgbu.ohmpi -DartifactId=index-core -Dversion=5.0 -Dpackaging=jar
call mvn install:install-file -Dfile=.\lib\mpi-client-patient.jar -DgroupId=oracle.hsgbu.ohmpi -DartifactId=mpi-client-patient -Dversion=5.0 -Dpackaging=jar
call mvn install:install-file -Dfile=.\lib\patient-ejb.jar -DgroupId=oracle.hsgbu.ohmpi -DartifactId=patient-ejb -Dversion=5.0 -Dpackaging=jar
