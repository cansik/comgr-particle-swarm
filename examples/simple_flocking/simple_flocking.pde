Flock flock;

float maxTargetDist = 200;
PVector target;

boolean ellipseOn = false;

ArrayList<Obstacle> obstacles;

void setup() {
  size(900, 600, P2D);
  flock = new Flock();
  // Add an initial set of boids into the system
  for (int i = 0; i < 200; i++) {
    flock.addBoid(new Boid(width/2, height/2));
  }

  //init obstacles
  obstacles = new ArrayList<Obstacle>();
  obstacles.add(new Obstacle(300, 400, 300, 50));
  obstacles.add(new Obstacle(600, 400, 50, 150));
  obstacles.add(new Obstacle(600, 200, 50, 100));
}

void draw() {
  background(50);

  if (target != null)
  {
    fill(100, 250, 100, 100);
    ellipse(target.x, target.y, maxTargetDist, maxTargetDist);
  }

  flock.run(obstacles);

  //render obstacles
  for (Obstacle o : obstacles)
    o.render();
}

// Add a new boid into the System
void mousePressed() {
  if (mouseButton == LEFT) {
    flock.addBoid(new Boid(mouseX, mouseY));
  } else if (mouseButton == RIGHT) {
    target = new PVector(mouseX, mouseY);
  }
}

void keyPressed() {

  float targetSpeed = 10;
  
  if(key == 'e')
  {
     ellipseOn = !ellipseOn;
  }
  
  if (key == CODED) {
    if (keyCode == UP) {
      target.add(new PVector(targetSpeed, 0));
    } else if (keyCode == DOWN) {
      target.sub(new PVector(targetSpeed, 0));
    } else if (keyCode == LEFT) {
      target.add(new PVector(0, targetSpeed));
    } else if (keyCode == RIGHT) {
      target.sub(new PVector(0, targetSpeed));
    }
  }
}



// The Flock (a list of Boid objects)

class Flock {
  ArrayList<Boid> boids; // An ArrayList for all the boids

    Flock() {
    boids = new ArrayList<Boid>(); // Initialize the ArrayList
  }

  void run(ArrayList<Obstacle> obstacles) {
    for (Boid b : boids) {
      b.run(boids, obstacles);  // Passing the entire list of boids to each boid individually
    }
  }

  void addBoid(Boid b) {
    boids.add(b);
  }
}

class Obstacle {
  PVector location;
  PVector size;

  public Obstacle(float x, float y, float w, float h)
  {
    location = new PVector(x, y);
    size = new PVector(w, h);
  }

  public void render()
  {
    fill(100, 100, 250, 100);
    rect(location.x, location.y, size.x, size.y);
  }

  public PVector getNearestPoint(PVector p)
  {
    float cx = max(min(p.x, location.x+size.x), location.x);
    float cy = max(min(p.y, location.y+size.y), location.y);

    return new PVector(cx, cy);
  }
}


// The Boid class

class Boid {

  PVector location;
  PVector velocity;
  PVector acceleration;
  float r;
  float maxforce;    // Maximum steering force
  float maxspeed;    // Maximum speed

    Boid(float x, float y) {
    acceleration = new PVector(0, 0);

    // This is a new PVector method not yet implemented in JS
    // velocity = PVector.random2D();

    // Leaving the code temporarily this way so that this example runs in JS
    float angle = random(TWO_PI);
    velocity = new PVector(cos(angle), sin(angle));

    location = new PVector(x, y);
    r = 2.0;
    maxspeed = 2;
    maxforce = 0.03;
  }

  void run(ArrayList<Boid> boids, ArrayList<Obstacle> obstacles) {
    flock(boids, obstacles);
    update();
    borders();
    render();
  }

  void applyForce(PVector force) {
    // We could add mass here if we want A = F / M
    acceleration.add(force);
  }

  // We accumulate a new acceleration each time based on three rules
  void flock(ArrayList<Boid> boids, ArrayList<Obstacle> obstacles) {
    PVector sep = separate(boids);   // Separation
    PVector ali = align(boids);      // Alignment
    PVector coh = cohesion(boids);   // Cohesion

    PVector rep = repulsion(obstacles); // Repulsion

    // Arbitrarily weight these forces
    sep.mult(1.5);
    ali.mult(1.0);
    coh.mult(1.0);
    rep.mult(0.0001); //like a wall

    // Add the force vectors to acceleration
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
    applyForce(rep);
  }

  // Method to update location
  void update() {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);

    //find target
    if (target != null && PVector.dist(location, target) < maxTargetDist)
    {
      velocity.add(seek(target));
    }

    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  // A method that calculates and applies a steering force towards a target
  // STEER = DESIRED MINUS VELOCITY
  PVector seek(PVector target) {
    PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target
    // Scale to maximum speed
    desired.normalize();
    desired.mult(maxspeed);

    // Above two lines of code below could be condensed with new PVector setMag() method
    // Not using this method until Processing.js catches up
    // desired.setMag(maxspeed);

    // Steering = Desired minus Velocity
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  // Limit to maximum steering force
    return steer;
  }

  void render() {
    // Draw a triangle rotated in the direction of velocity
    float theta = velocity.heading2D() + radians(90);
    // heading2D() above is now heading() but leaving old syntax until Processing.js catches up

    //draw ellipse with infos
    fill(250, 100, 100, 50);
    
    if(ellipseOn)
      ellipse(location.x, location.y, 25, 25);

    fill(255, 240, 100);
    stroke(255);
    pushMatrix();
    translate(location.x, location.y);
    rotate(theta);
    beginShape(TRIANGLES);
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape();
    popMatrix();
  }

  // Wraparound
  void borders() {
    if (location.x < -r) location.x = width+r;
    if (location.y < -r) location.y = height+r;
    if (location.x > width+r) location.x = -r;
    if (location.y > height+r) location.y = -r;
  }

  PVector repulsion(ArrayList<Obstacle> obstacles)
  {
    float minObstacleDist = 100;
    float breakConstant = 200000;
    PVector repulsion = new PVector(0, 0);

    for (Obstacle o : obstacles)
    {
      PVector nearest = o.getNearestPoint(location);
      float dist = location.dist(nearest);

      if (dist < minObstacleDist)
      {
        //draw for debugging
        stroke(204, 255, 255);
        //line(location.x, location.y, nearest.x, nearest.y);

        PVector seek = seek(nearest);
        seek.mult(min(breakConstant/dist, Float.MAX_VALUE));
        seek.mult(-1.0); // invert for repulusion

        //System.out.println("X: " + seek.x + " Y: " + seek.y);

        repulsion.add(seek);
      }
    }

    return repulsion;
  }

  // Separation
  // Method checks for nearby boids and steers away
  PVector separate (ArrayList<Boid> boids) {
    float desiredseparation = 25.0f;
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    // For every boid in the system, check if it's too close
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((d > 0) && (d < desiredseparation)) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(location, other.location);
        diff.normalize();
        diff.div(d);        // Weight by distance
        steer.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // steer.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  PVector align (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxspeed);

      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } else {
      return new PVector(0, 0);
    }
  }

  // Cohesion
  // For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
  PVector cohesion (ArrayList<Boid> boids) {
    float neighbordist = 50;
    PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
    int count = 0;
    for (Boid other : boids) {
      float d = PVector.dist(location, other.location);
      if ((d > 0) && (d < neighbordist)) {
        sum.add(other.location); // Add location
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return seek(sum);  // Steer towards the location
    } else {
      return new PVector(0, 0);
    }
  }
}