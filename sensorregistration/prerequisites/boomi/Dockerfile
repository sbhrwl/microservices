FROM openjdk:11-jre-slim

# Environment
ENV BOOMI_ATOM_NAME=MyAtom \
    BOOMI_ACCOUNT_ID=your_account_id \
    BOOMI_USERNAME=your_username \
    BOOMI_PASSWORD=your_password \
    BOOMI_ENVIRONMENT_ID=your_env_id \
    BOOMI_INSTALL_DIR=/opt/boomi

# Copy installer into image
COPY AtomInstaller64.sh /tmp/AtomInstaller64.sh
RUN chmod +x /tmp/AtomInstaller64.sh

# Install Boomi Atom
RUN /tmp/AtomInstaller64.sh \
    installAtom /opt/boomi \
    -V atomName=${BOOMI_ATOM_NAME} \
    -V accountId=${BOOMI_ACCOUNT_ID} \
    -V username=${BOOMI_USERNAME} \
    -V password=${BOOMI_PASSWORD} \
    -q

EXPOSE 9090
CMD ["sh", "-c", "cd /opt/boomi && ./atom start && tail -f logs/atom.log"]
