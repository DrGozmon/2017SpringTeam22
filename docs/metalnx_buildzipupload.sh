# Maven build, zip and upload the file to the Metalnx server
# latest-metalnx-web/

absdir='/home/usr/Development/rhpajers/MetalnxDevelopment/'
mlxdir='metalnx-web/'
unitypassword=''

# Change directory to the maven folder
cd $absdir$mlxdir\src/
:

# Run a maven build
mvn clean generate-sources package -Ppreprod -Dmaven.test.skip=true

echo "---> Maven Build Done, zipping..."

# Remove older zipped file
rm $absdir\metalnx-web.tar.gz

# Zip the file
tar -zcf $absdir\metalnx-web.tar.gz -C $absdir$mlxdir .

echo "---> Zipping Done, Uploading..."

# Push it up to the Metalnx server (sd-vm15)
sshpass -p "$unitypassword" scp $absdir\metalnx-web.tar.gz rhpajers@sd-vm15.csc.ncsu.edu:/home/rhpajers/compilationfolder

echo "---> Upload Done"
