// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// (c) December 2023 team 4450 (cw)
package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive.WheelSpeeds;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


public class Robot extends TimedRobot {
  // drive motors ====================================================
  private final WPI_TalonSRX m_leftMotor1 = new WPI_TalonSRX(1);
  private final WPI_TalonSRX m_leftMotor2 = new WPI_TalonSRX(2);
  private final WPI_TalonSRX m_rightMotor1 = new WPI_TalonSRX(3);
  private final WPI_TalonSRX m_rightMotor2 = new WPI_TalonSRX(4);
  private final DifferentialDrive drivebase = new DifferentialDrive(m_leftMotor1, m_rightMotor1);
  
  // eye and arm motors ==============================================
  private final Talon armX = new Talon(0);
  private final Talon armY = new Talon(1);
  private final Talon armB = new Talon(2);
  private final Talon armA = new Talon(3);
  private final Talon eye = new Talon(4);

  // eye and arm toggles ==============================================
  private boolean armXtoggle = false;
  private boolean armYtoggle = false;
  private boolean armAtoggle = false;
  private boolean armBtoggle = false;
  private boolean eyetoggle = false;

  // other ============================================================
  private final ShuffleboardData shuffleboard = new ShuffleboardData(drivebase);
  private double counter = 0;
  private XboxController joystick;

  @Override
  public void robotInit() {
    // setup drive motors
    m_rightMotor1.setInverted(true);
    m_rightMotor2.setInverted(true);
    m_leftMotor2.follow(m_leftMotor1);
    m_rightMotor2.follow(m_rightMotor1);

    // setup arm motors
    armA.setInverted(true);
    armY.setInverted(true);

    // setup controller
    joystick = new XboxController(0);
  }

  @Override
  public void teleopPeriodic() {
    checkButtons();
    moveAttachments();
    drive();
    shuffleboard.voltage.setDouble(RobotController.getBatteryVoltage());
  }

  @Override
  public void autonomousPeriodic() {
   moveAttachments();
   shuffleboard.voltage.setDouble(RobotController.getBatteryVoltage());
  }

  @Override
  public void autonomousInit() {
    armAtoggle = true;
    armBtoggle = true;
    armXtoggle = true;
    armYtoggle = true;
    eyetoggle = true;
  }
  @Override
  public void disabledInit() {
    armAtoggle = false;
    armBtoggle = false;
    armXtoggle = false;
    armYtoggle = false;
    eyetoggle = false;
    drivebase.tankDrive(0, 0); // reset to not moving
  }
  @Override
  public void disabledPeriodic() {
    shuffleboard.voltage.setDouble(RobotController.getBatteryVoltage());
  }

  private void checkButtons() {
    if (joystick.getAButtonPressed()) {armAtoggle = !armAtoggle;}
    if (joystick.getBButtonPressed()) {armBtoggle = !armBtoggle;}
    if (joystick.getXButtonPressed()) {armXtoggle = !armXtoggle;}
    if (joystick.getYButtonPressed()) {armYtoggle = !armYtoggle;}
    if (joystick.getBackButtonPressed()) {eyetoggle = !eyetoggle;}

    if (joystick.getStartButtonPressed()) {
      if (armAtoggle || armBtoggle || armXtoggle || armYtoggle) {
        armAtoggle = false;
        armBtoggle = false;
        armXtoggle = false;
        armYtoggle = false;
      }
      else {
        armAtoggle = true;
        armBtoggle = true;
        armXtoggle = true;
        armYtoggle = true;
      }
    }
  }
  private void moveAttachments() { 
    counter += 0.01;
    if (counter >= (2 * Math.PI)) {counter = 0.0;}
       
    // arms ------------------
    double armApower = 0; if (armAtoggle) {armApower = 0.3 + 0.2 * Math.sin(counter);}
    double armBpower = 0; if (armBtoggle) {armBpower = 0.3 + 0.2 * Math.cos(counter+0.22);}
    double armXpower = 0; if (armXtoggle) {armXpower = 0.3 + 0.2 * Math.sin(counter);}
    double armYpower = 0; if (armYtoggle) {armYpower = 0.3 + 0.2 * Math.cos(counter+0.22);}

    armA.set(armApower);
    armB.set(armBpower);
    armX.set(armXpower);
    armY.set(armYpower);

    shuffleboard.armA_speed.setDouble(armApower);
    shuffleboard.armB_speed.setDouble(armBpower);
    shuffleboard.armX_speed.setDouble(armXpower);
    shuffleboard.armY_speed.setDouble(armYpower);
    
    // eye -------------------
    if (eyetoggle) {eye.set(shuffleboard.eye_speed.getDouble(0.1));} else {eye.set(0);}

    // set shuffleboard ----------
    shuffleboard.armA_on.setBoolean(armAtoggle);
    shuffleboard.armB_on.setBoolean(armBtoggle);
    shuffleboard.armX_on.setBoolean(armXtoggle);
    shuffleboard.armY_on.setBoolean(armYtoggle);
    shuffleboard.eye_on.setBoolean(eyetoggle);
  }
  private void drive() {
    drivebase.setMaxOutput(shuffleboard.max_speed.getDouble(0.8));
    drivebase.setDeadband(shuffleboard.deadband_zone.getDouble(0.05));
    switch (shuffleboard.getDriveMode()) {
      case "curve":
        drivebase.curvatureDrive(joystick.getLeftY(), joystick.getRightX(), joystick.getRightBumper());
        break;
      case "arcade":
        drivebase.arcadeDrive(joystick.getRightY(), joystick.getRightX(), true);
        break;
      case "tank":
        drivebase.tankDrive(joystick.getLeftY(), joystick.getRightY(), true);
        break;
    }
  }
}
