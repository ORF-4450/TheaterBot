// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// (c) December 2023 team 4450 (cw)
package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.motorcontrol.Talon;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;


public class Robot extends TimedRobot {
  private XboxController controller;

  private final TalonSRX m_leftMotor1 = new TalonSRX(1);
  private final TalonSRX m_leftMotor2 = new TalonSRX(2);
  private final TalonSRX m_rightMotor1 = new TalonSRX(3);
  private final TalonSRX m_rightMotor2 = new TalonSRX(4);

  private final Talon armX = new Talon(0);
  private final Talon armY = new Talon(1);
  private final Talon armA = new Talon(3);
  private final Talon armB = new Talon(2);

  private final Talon eye = new Talon(4);

  private boolean armXtoggle = false;
  private boolean armYtoggle = false;
  private boolean armAtoggle = false;
  private boolean armBtoggle = false;
  private boolean alltoggle = false;

  private boolean eyetoggle = false;
  private double counter = 0;
  
  @Override
  public void robotInit() {
    m_rightMotor1.setInverted(true);
    m_rightMotor2.setInverted(true);
    m_leftMotor2.follow(m_leftMotor1);
    m_rightMotor2.follow(m_rightMotor1);

    armA.setInverted(true);
    armY.setInverted(true);

    controller = new XboxController(0);
  }

  @Override
  public void teleopPeriodic() {
    counter += 0.01;
    if (counter >= (2 * Math.PI)) {counter = 0.0;}

    if (controller.getAButtonPressed()) {armAtoggle = !armAtoggle;}
    if (controller.getBButtonPressed()) {armBtoggle = !armBtoggle;}
    if (controller.getXButtonPressed()) {armXtoggle = !armXtoggle;}
    if (controller.getYButtonPressed()) {armYtoggle = !armYtoggle;}
    if (controller.getBackButtonPressed()) {eyetoggle = !eyetoggle;}

    if (controller.getStartButtonPressed()) {
      if (alltoggle) {
        armAtoggle = false;
        armBtoggle = false;
        armXtoggle = false;
        armYtoggle = false;
        alltoggle = false;
      }
      else {
        armAtoggle = true;
        armBtoggle = true;
        armXtoggle = true;
        armYtoggle = true;
        alltoggle = true;
      }
    }

    double plusminus1 = 0.2 * Math.sin(counter);
    double plusminus2 = 0.2 * Math.cos(counter+0.22);
    
    if (armAtoggle) {armA.set(0.3 + plusminus1);} else {armA.set(0);}
    if (armBtoggle) {armB.set(0.3 + plusminus2);} else {armB.set(0);}
    if (armXtoggle) {armX.set(0.3 + plusminus1);} else {armX.set(0);}
    if (armYtoggle) {armY.set(0.3 + plusminus2);} else {armY.set(0);}
    if (eyetoggle) {eye.set(0.1);} else {eye.set(0);}
  
    m_rightMotor1.set(ControlMode.PercentOutput, controller.getLeftY());
    m_leftMotor1.set(ControlMode.PercentOutput, controller.getRightY());
    
  }
}
