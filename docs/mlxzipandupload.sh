# Maven build, zip and upload the file to the Metalnx server

# Change directory to the maven folder
cd /home/usr/Development/rhpajers/metalnx-web/src/
:

# Run a maven build
mvn clean generate-sources package -Ppreprod -Dmaven.test.skip=true

echo "---> Maven Build Done, zipping..."

# Remove older zipped file
rm /home/usr/Development/rhpajers/metalnx-web.tar.gz

# Zip the file
tar -zcf /home/usr/Development/rhpajers/metalnx-web.tar.gz -C /home/usr/Development/rhpajers/metalnx-web/ .

echo "---> Zipping Done, Uploading..."

# Push it up to the Metalnx server (sd-vm15)
sshpass -p "" scp /home/usr/Development/rhpajers/metalnx-web.tar.gz rhpajers@sd-vm15.csc.ncsu.edu:/home/rhpajers/compilationfolder

echo "---> Upload Done"
