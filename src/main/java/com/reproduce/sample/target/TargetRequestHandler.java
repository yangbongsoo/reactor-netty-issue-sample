package com.reproduce.sample.target;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetRequestHandler implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(TargetRequestHandler.class);
	private Socket connection;

	public TargetRequestHandler(Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		log.debug("New client! Connected IP : {}, port : {}", connection.getInetAddress(), connection.getPort());
		OutputStream out = null;
		InputStream in = null;
		try {
			in = connection.getInputStream();
			out = connection.getOutputStream();

			// request
			DataInputStream dis = new DataInputStream(in);
			byte[] byteArr = new byte[25000];
			int readByteCount = dis.read(byteArr);
			log.info("readByteCount : {}", readByteCount);
			String data = new String(byteArr, 0, readByteCount, "UTF-8");
			System.out.println("request data : " + data);

			// response
			DataOutputStream dos = new DataOutputStream(out);
			byte[] responseBody = ("{\n"
							+ "  \"id\": \"ybs\"\n"
							+ "}").getBytes();
			response200Header(dos, responseBody.length);
			responseBody(dos, responseBody);
		} catch (IOException e) {
			// ignore
		} finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
	}

	private void response503Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 503 Service Unavailable \r\n");
			dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void response500Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 500 Internal Server Error \r\n");
			dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void response400Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 400 Bad Request \r\n");
			dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			// dos.writeBytes("Content-Type: application/x-www-form-urlencoded\r\n");
			dos.writeBytes("Content-Type: application/json\r\n");
			// dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
