package net.minecraft.client;

import java.awt.Canvas;
import java.awt.Component;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;

import net.minecraft.client.a.c.AnimationHandlerSuper;
import net.minecraft.client.a.c.WaterAnimationHandler2;
import net.minecraft.client.a.c.FireAnimationHandler;
import net.minecraft.client.a.c.WaterAnimationHandler;
import net.minecraft.client.a.c.CogAnimationHandler;
import net.minecraft.client.a.c.LavaAnimationHandler;
import net.minecraft.client.a.TerrainRenderer;
import net.minecraft.client.a.IsometricScreenshots;
import net.minecraft.client.a.GLTexturing2;
import net.minecraft.client.g.LocalPlayer;
import net.minecraft.a.a.b.Block;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.client.NMCRender1;
import net.minecraft.client.NMCEnum;
import net.minecraft.client.RenderTimer;
import net.minecraft.client.EmptyClass1;
import net.minecraft.client.ResourceDownloader;
import net.minecraft.client.BlockLoader;
import net.minecraft.client.CrossbarHandler;
import net.minecraft.client.TimerHackThread;
import net.minecraft.client.UnknownClass1;
import net.minecraft.client.UnknownError1;
import net.minecraft.client.OptionsDefault;
import net.minecraft.client.MainMenu;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

//Internal server?
//net.minecraft.client.d
public final class InternalServer implements Runnable {
	   public net.minecraft.client.d.UnknownSoundClass1 a = new net.minecraft.client.d.UnknownSoundClass2(this);
   private boolean E = false;
   public int b;
   public int c;
   private EmptyClass1 F;
   private RenderTimer G = new RenderTimer(20.0F);
   public net.minecraft.a.a.WorldHandler d;
   public TerrainRenderer e;
   public net.minecraft.client.g.LocalPlayer f;
   public net.minecraft.client.f.ParticleRenderer g;
   public BlockLoader h = null;
   public String i;
   public Canvas j;
   public boolean k = true;
   public volatile boolean l = false;
   public GLTexturing2 m;
   public net.minecraft.client.c.GLRender1 n;
   public net.minecraft.client.c.MenuBackground o = null;
   public NMCRender1 p = new NMCRender1(this);
   public IsometricScreenshots q = new IsometricScreenshots(this);
   private ResourceDownloader H;
   private int I = 0;
   private int J = 0;
   private int K;
   private int L;
   public String r = null;
   public int s = 0;
   public net.minecraft.client.c.IngameHUD t;
   public boolean u = false;
   public net.minecraft.a.d.adClass3 v;
   public OptionsDefault w;
   private MinecraftApplet M;
   public net.minecraft.client.e.SoundPlayer x;
   public CrossbarHandler y;
   public File z;
   private String N;
   private WaterAnimationHandler O;
   private LavaAnimationHandler P;
   volatile boolean A;
   public String B;
   public boolean C;
   private int Q;
   public boolean D;

   public InternalServer(Canvas var1, MinecraftApplet var2, int var3, int var4, boolean var5) {
      new net.minecraft.client.b.CollisionMath8(0.0F);
      this.v = null;
      this.x = new net.minecraft.client.e.SoundPlayer();
      this.N = null;
      this.O = new WaterAnimationHandler();
      this.P = new LavaAnimationHandler();
      this.A = false;
      this.B = "";
      this.C = false;
      this.Q = 0;
      this.D = false;
      this.K = var3;
      this.L = var4;
      this.E = var5;
      this.M = var2;
      new TimerHackThread(this, "Timer hack thread");
      this.j = var1;
      this.b = var3;
      this.c = var4;
      this.E = var5;
   }

   public final void a(String var1, int var2) {
      this.N = var1;
   }

   public final void a(net.minecraft.client.c.MenuBackground var1) {
      if(!(this.o instanceof net.minecraft.client.c.UnknownMenu1)) {
         if(this.o != null) {
            this.o.a();
         }

         if(var1 == null && this.d == null) {
        	 var1 = new MainMenu();
         } else if(var1 == null && this.f.W <= 0) {
        	 var1 = new net.minecraft.client.c.MenuIngame();        	 
         }

         this.o = (net.minecraft.client.c.MenuBackground)var1;
         if(var1 != null) {
            this.e();
            net.minecraft.client.c.UnknownClass4 var2;
            int var3 = (var2 = new net.minecraft.client.c.UnknownClass4(this.b, this.c)).a();
            int var4 = var2.b();
            ((net.minecraft.client.c.MenuBackground) var1).a(this, var3, var4);
            this.u = false;
         } else {
            this.b();
         }
      }

   }

   public final void a() {
	   this.shutdown();
   }

   public final void shutdown() {
	   try {
		   if(this.H != null) {
			   this.H.a();
		   }
	   } catch (Exception var5) {
		   ;
	   }

	   boolean var4 = false;

	   try {
		   var4 = true;
		   this.x.b();
		   Mouse.destroy();
		   Keyboard.destroy();
		   var4 = false;
	   } finally {
		   if(var4) {
			   Display.destroy();
		   }

	   }

	   Display.destroy();
   }

   public final void run() {
	   this.A = true;

	   try {
		   InternalServer var24 = this;
		   if(this.j != null) {
			   Display.setParent(this.j);
		   } else if(this.E) {
			   Display.setFullscreen(true);
			   this.b = Display.getDisplayMode().getWidth();
			   this.c = Display.getDisplayMode().getHeight();
		   } else {
			   Display.setDisplayMode(new DisplayMode(this.b, this.c));
		   }

		   Display.setTitle("Minecraft Minecraft Indev");

		   try {
			   Display.create();
			   System.out.println("LWJGL version: " + Sys.getVersion());
			   System.out.println("GL RENDERER: " + GL11.glGetString(7937));
			   System.out.println("GL VENDOR: " + GL11.glGetString(7936));
			   System.out.println("GL VERSION: " + GL11.glGetString(7938));
			   ContextCapabilities var3 = GLContext.getCapabilities();
			   System.out.println("OpenGL 3.0: " + var3.OpenGL30);
			   System.out.println("OpenGL 3.1: " + var3.OpenGL31);
			   System.out.println("OpenGL 3.2: " + var3.OpenGL32);
			   System.out.println("ARB_compatibility: " + var3.GL_ARB_compatibility);
			   if(var3.OpenGL32) {
				   IntBuffer var28 = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
				   GL11.glGetInteger('鄦', var28);
				   int exc = var28.get(0);
				   System.out.println("PROFILE MASK: " + Integer.toBinaryString(exc));
				   System.out.println("CORE PROFILE: " + ((exc & 1) != 0));
				   System.out.println("COMPATIBILITY PROFILE: " + ((exc & 2) != 0));
			   }
		   } catch (LWJGLException var20) {
			   var20.printStackTrace();

			   try {
				   Thread.sleep(1000L);
			   } catch (InterruptedException var19) {
				   ;
			   }

			   Display.create();
		   }

		   Keyboard.create();
		   Mouse.create();
		   this.y = new CrossbarHandler(this.j);

		   try {
			   Controllers.create();
		   } catch (Exception var18) {
			   var18.printStackTrace();
		   }

		   GL11.glEnable(3553);
		   GL11.glShadeModel(7425);
		   GL11.glClearDepth(1.0D);
		   GL11.glEnable(2929);
		   GL11.glDepthFunc(515);
		   GL11.glEnable(3008);
		   GL11.glAlphaFunc(516, 0.1F);
		   GL11.glCullFace(1029);
		   GL11.glMatrixMode(5889);
		   GL11.glLoadIdentity();
		   GL11.glMatrixMode(5888);
		   this.F = new EmptyClass1();
		   String var281 = "minecraft";
		   String var29 = System.getProperty("user.home", ".");
		   File var21;
		   String var311;
		   switch(UnknownClass1.a[((var311 = System.getProperty("os.name").toLowerCase()).contains("win")?NMCEnum.c:(var311.contains("mac")?NMCEnum.d:(var311.contains("solaris")?NMCEnum.b:(var311.contains("sunos")?NMCEnum.b:(var311.contains("linux")?NMCEnum.a:(var311.contains("unix")?NMCEnum.a:NMCEnum.e)))))).ordinal()]) {
		   case 1:
		   case 2:
			   var21 = new File(var29, '.' + var281 + '/');
			   break;
		   case 3:
			   if((var311 = System.getenv("APPDATA")) != null) {
				   var21 = new File(var311, "." + var281 + '/');
			   } else {
				   var21 = new File(var29, '.' + var281 + '/');
			   }
			   break;
		   case 4:
			   var21 = new File(var29, "Library/Application Support/" + var281);
			   break;
		   default:
			   var21 = new File(var29, var281 + '/');
		   }

		   if(!var21.exists() && !var21.mkdirs()) {
			   throw new RuntimeException("The working directory could not be created: " + var21);
		   }

		   this.z = var21;
		   this.w = new OptionsDefault(this, this.z);
		   this.x.a(this.w);
		   this.m = new net.minecraft.client.a.GLTexturing2(this.w);
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)this.P);
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)this.O);
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)(new net.minecraft.client.a.c.WaterAnimationHandler2()));
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)(new net.minecraft.client.a.c.FireAnimationHandler(0)));
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)(new net.minecraft.client.a.c.FireAnimationHandler(1)));
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)(new net.minecraft.client.a.c.CogAnimationHandler(0)));
		   this.m.a((net.minecraft.client.a.c.AnimationHandlerSuper)(new net.minecraft.client.a.c.CogAnimationHandler(1)));
		   this.n = new net.minecraft.client.c.GLRender1(this.w, "/default.png", this.m);
		   BufferUtils.createIntBuffer(256).clear().limit(256);
		   this.e = new net.minecraft.client.a.TerrainRenderer(this, this.m);
		   GL11.glViewport(0, 0, this.b, this.c);
		   if(this.N != null && this.h != null) {
			   net.minecraft.a.a.WorldHandler var31;
			   (var31 = new net.minecraft.a.a.WorldHandler()).a(8, 8, 8, new byte[512], new byte[512]);
			   this.a(var31);
		   } else if(this.d == null) {
			   this.a((net.minecraft.client.c.MenuBackground) (new MainMenu()));
		   }

		   this.g = new net.minecraft.client.f.ParticleRenderer(this.d, this.m);

		   try {
			   var24.H = new ResourceDownloader(var24.z, var24);
			   var24.H.start();
		   } catch (Exception var17) {
			   ;
		   }

		   this.t = new net.minecraft.client.c.IngameHUD(this);
	   } catch (Exception var26) {
		   var26.printStackTrace();
		   JOptionPane.showMessageDialog((Component)null, var26.toString(), "Failed to start Minecraft", 0);
		   return;
	   }

	   System.out.println("Indev Patch v2 by InsanityBringer, with tech help by Lahwran\nDesigned for MCNostalgia, with love <3");
	   long var27 = System.currentTimeMillis();
	   int var30 = 0;

	   try {
		   label455:
			   while(true) {
				   boolean var32 = false;

				   label452: {
					   try {
						   if(this.A) {
							   if(this.d != null) {
								   this.d.d();
							   }

							   if(this.j == null && Display.isCloseRequested()) {
								   this.A = false;
							   }

							   try {
								   if(this.l) {
									   float var33 = this.G.c;
									   this.G.a();
									   this.G.c = var33;
								   } else {
									   this.G.a();
								   }

								   for(int var34 = 0; var34 < this.G.b; ++var34) {
									   ++this.I;
									   this.f();
								   }

								   this.x.a(this.f, this.G.c);
								   GL11.glEnable(3553);
								   this.a.a(this.G.c);
								   this.q.a(this.G.c);
								   if(!Display.isActive()) {
									   if(this.E) {
										   this.d();
									   }

									   Thread.sleep(10L);
								   }

								   if(this.j != null && !this.E && (this.j.getWidth() != this.b || this.j.getHeight() != this.c)) {
									   this.b = this.j.getWidth();
									   this.c = this.j.getHeight();
                           this.a(this.b, this.c);
                        }

                        if(this.w.h) {
                           Thread.sleep(5L);
                        }

                        ++var30;
                        this.l = this.o != null && this.o.c();
                     } catch (Exception var211) {
                        this.a((net.minecraft.client.c.MenuBackground)(new net.minecraft.client.c.UnknownMenu1("Client error", "The game broke! [" + var211 + "]")));
                        var211.printStackTrace();
                        var32 = false;
                        break;
                     }

                     while(true) {
                        if(System.currentTimeMillis() < var27 + 1000L) {
                           continue label455;
                        }

                        this.B = var30 + " fps, " + net.minecraft.client.a.GLTexturingHandler.a + " chunk updates";
                        net.minecraft.client.a.GLTexturingHandler.a = 0;
                        var27 += 1000L;
                        var30 = 0;
                     }
                  }

                  var32 = false;
                  break;
               } catch (UnknownError1 var22) {
                  var32 = false;
                  break label452;
               } catch (Exception var23) {
                  var23.printStackTrace();
                  var32 = false;
               } finally {
                  if(var32) {
                     this.shutdown();
                  }

               }

               this.shutdown();
               return;
            }

            this.shutdown();
            return;
         }

         this.shutdown();
      } catch (Exception var25) {
         System.out.printf("OMG ERROR!\n", new Object[0]);
         var25.printStackTrace();
      }

   }

   public final void b() {
      if(Display.isActive() && !this.C) {
         this.C = true;
         this.y.a();
         this.a((net.minecraft.client.c.MenuBackground) null);
         this.Q = this.I + 10000;
      }

   }

   private void e() {
      if(this.C) {
         if(this.f != null) {
            net.minecraft.client.g.LocalPlayer var2 = this.f;
            this.f.a.b();
         }

         this.C = false;

         try {
            Mouse.setNativeCursor((Cursor)null);
         } catch (LWJGLException var21) {
            var21.printStackTrace();
         }
      }

   }

   public final void c() {
      if(this.o == null) {
         this.a((net.minecraft.client.c.MenuBackground)(new net.minecraft.client.c.IngameMenu()));
      }

   }

   private void a(int var1) {
      if(var1 != 0 || this.J <= 0) {
         if(var1 == 0) {
             this.q.a.c();
         }

         net.minecraft.a.b.NBTTag var2;
         int var3;
         net.minecraft.a.a.WorldHandler var5;
         if(var1 == 1 && (var2 = this.f.b.d()) != null) {
            var3 = var2.a;
            net.minecraft.client.g.LocalPlayer var9 = this.f;
            var5 = this.d;
            net.minecraft.a.b.NBTTag var10;
            if((var10 = var2.a().a(var2, var5, var9)) != var2 || var10 != null && var10.a != var3) {
               this.f.b.a[this.f.b.c] = var10;
               this.q.a.d();
               if(var10.a == 0) {
                  this.f.b.a[this.f.b.c] = null;
               }
            }
         }

         if(this.v == null) {
            if(var1 == 0 && !(this.a instanceof net.minecraft.client.d.UnknownSoundClass3)) {
                this.J = 10;
             }
          } else {
             net.minecraft.a.b.NBTTag var91;
             if(this.v.a == 1) {
                if(var1 == 0) {
                   net.minecraft.a.c.EntityGeneric var101 = this.v.g;
                   net.minecraft.client.g.LocalPlayer var13 = this.f;
                   net.minecraft.a.c.e.Inventory var15 = this.f.b;
                   int var6 = (var91 = this.f.b.a(var15.c)) != null?net.minecraft.a.b.ItemStack.b[var91.c].a():1;
                   if(var6 > 0) {
                      var101.a(var13, var6);
                      if((var2 = var13.b.d()) != null && var101 instanceof net.minecraft.a.c.EntityLiving) {
                         net.minecraft.a.c.EntityLiving var18 = (net.minecraft.a.c.EntityLiving)var101;
                         net.minecraft.a.b.ItemStack.b[var2.c].a(var2);
                         if(var2.a <= 0) {
                            var13.h_();
                         }
                     }
                  }

                  return;
               }
            } else if(this.v.a == 0) {
               int var102 = this.v.b;
               var3 = this.v.c;
               int var131 = this.v.d;
               int var151 = this.v.e;
               Block var61 = Block.c[this.d.a(var102, var3, var131)];
               if(var1 == 0) {
                  this.d.g(var102, var3, var131, this.v.e);
                  if(var61 != Block.BedrockBlock) {
                     this.a.a(var102, var3, var131);
                     return;
                  }
               } else {
                   var91 = this.f.b.d();
                   int var16;
                   if((var16 = this.d.a(var102, var3, var131)) > 0 && Block.c[var16].a(this.d, var102, var3, var131, (net.minecraft.a.c.e.EntityPlayer)this.f)) {
                       return;
                   }

                   if(var91 == null) {
                      return;
                   }

                   var16 = var91.a;
                   var5 = this.d;
                   if(var91.a().a(var91, var5, var102, var3, var131, var151)) {
                      this.q.a.c();
                   }

                   if(var91.a == 0) {
                      this.f.b.a[this.f.b.c] = null;
                      return;
                   }

                   if(var91.a != var16) {
                      this.q.a.b();
                   }
                }
             }
          }
       }

    }

    public final void d() {
       try {
          this.E = !this.E;
          System.out.println("Toggle fullscreen!");
          if(this.E) {
             Display.setDisplayMode(Display.getDesktopDisplayMode());
             this.b = Display.getDisplayMode().getWidth();
             this.c = Display.getDisplayMode().getHeight();
          } else {
             if(this.j != null) {
                this.b = this.j.getWidth();
                this.c = this.j.getHeight();
             } else {
                this.b = this.K;
                this.c = this.L;
             }

             Display.setDisplayMode(new DisplayMode(this.K, this.L));
          }

          this.e();
          Display.setFullscreen(this.E);
          Display.update();
          Thread.sleep(1000L);
          if(this.E) {
             this.b();
          }

          if(this.o != null) {
             this.e();
             this.a(this.b, this.c);
          }

          System.out.println("Size: " + this.b + ", " + this.c);
       } catch (Exception var2) {
          var2.printStackTrace();
       }

    }

    private void a(int var1, int var2) {
       this.b = var1;
       this.c = var2;
       if(this.o != null) {
          net.minecraft.client.c.UnknownClass4 var3;
          var2 = (var3 = new net.minecraft.client.c.UnknownClass4(var1, var2)).a();
          var1 = var3.b();
          this.o.a(this, var2, var1);
       }

    }

    private void f() {
       this.t.a();
       if(!this.l && this.d != null) {
          this.a.c();
       }

       try {
          GL11.glBindTexture(3553, this.m.a("/terrain.png"));
       } catch (NullPointerException var71) {
          var71.printStackTrace();
       }

       if(!this.l) {
          this.m.a();
       }

       if(this.o == null && this.f != null && this.f.W <= 0) {
          this.a((net.minecraft.client.c.MenuBackground)null);
       }

       if(this.o == null || this.o.f) {
          label292:
          while(true) {
             while(true) {
                int var6;
                int var2;
                while(Mouse.next()) {
                   if((var6 = Mouse.getEventDWheel()) != 0) {
                      var2 = var6;
                      net.minecraft.a.c.e.Inventory var3 = this.f.b;
                      if(var6 > 0) {
                         var2 = 1;
                      }

                      if(var2 < 0) {
                         var2 = -1;
                      }

                      for(var3.c -= var2; var3.c < 0; var3.c += 9) {
                         ;
                      }

                      while(var3.c >= 9) {
                         var3.c -= 9;
                      }
                   }

                   if(this.o == null) {
                      if(!this.C && Mouse.getEventButtonState()) {
                         this.b();
                      } else {
                         if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                            this.a(0);
                            this.Q = this.I;
                         }

                         if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
                            this.a(1);
                            this.Q = this.I;
                         }

                         if(Mouse.getEventButton() == 2 && Mouse.getEventButtonState() && this.v != null) {
                            if((var2 = this.d.a(this.v.b, this.v.c, this.v.d)) == Block.GrassBlock.at) {
                               var2 = Block.DirtBlock.at;
                            }

                            if(var2 == Block.DoubleSlabBlock.at) {
                               var2 = Block.SlabBlock.at;
                            }

                            if(var2 == Block.BedrockBlock.at) {
                               var2 = Block.StoneBlock.at;
                            }

                            this.f.b.b(var2);
                         }
                      }
                   } else if(this.o != null) {
                      this.o.f();
                   }
                }

                if(this.J > 0) {
                   --this.J;
                }

                while(true) {
                   while(true) {
                      do {
                         int var7;
                         boolean var9;
                         if(!Keyboard.next()) {
                            if(this.o == null) {
                               if(Mouse.isButtonDown(0) && (float)(this.I - this.Q) >= this.G.a / 4.0F && this.C) {
                                  this.a(0);
                                  this.Q = this.I;
                               }

                               if(Mouse.isButtonDown(1) && (float)(this.I - this.Q) >= this.G.a / 4.0F && this.C) {
                                  this.a(1);
                                  this.Q = this.I;
                               }
                            }

                            var9 = this.o == null && Mouse.isButtonDown(0) && this.C;
                            boolean var10 = false;
                            if(!this.a.b && this.J <= 0) {
                               if(var9 && this.v != null && this.v.a == 0) {
                                  var2 = this.v.b;
                                  var7 = this.v.c;
                                  int var4 = this.v.d;
                                  this.a.a(var2, var7, var4, this.v.e);
                                  this.g.a(var2, var7, var4, this.v.e);
                               } else {
                                  this.a.a();
                               }
                            }
                            break label292;
                         }

                         net.minecraft.client.g.LocalPlayer var8 = this.f;
                         var7 = Keyboard.getEventKey();
                         var9 = Keyboard.getEventKeyState();
                         var8.a.a(var7, var9);
                      } while(!Keyboard.getEventKeyState());

                      if(Keyboard.getEventKey() == 87) {
                         this.d();
                      } else {
                         if(this.o != null) {
                            this.o.g();
                         } else {
                            if(Keyboard.getEventKey() == 1) {
                               this.c();
                            }

                            if(Keyboard.getEventKey() == 65) {
                               this.q.b();
                            }

                            if(this.a instanceof net.minecraft.client.d.UnknownSoundClass3) {
                               if(Keyboard.getEventKey() == this.w.r.b) {
                                  this.f.j();
                               }

                               if(Keyboard.getEventKey() == this.w.q.b) {
                                  this.d.a((int)this.f.h, (int)this.f.i, (int)this.f.j, this.f.n);
                                  this.f.j();
                               }
                            }

                            if(Keyboard.getEventKey() == 63) {
                               this.w.v = !this.w.v;
                            }

                            if(Keyboard.getEventKey() == this.w.n.b) {
                               this.a((net.minecraft.client.c.MenuBackground)(new net.minecraft.client.c.a.GUIMath2(this.f.b)));
                            }

                            if(Keyboard.getEventKey() == this.w.o.b) {
                               this.f.a(this.f.b.a(this.f.b.c, 1), false);
                            }
                         }

                         for(var6 = 0; var6 < 9; ++var6) {
                            if(Keyboard.getEventKey() == var6 + 2) {
                               this.f.b.c = var6;
                            }
                         }

                         if(Keyboard.getEventKey() == this.w.p.b) {
                            this.w.b(4, !Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)?1:-1);
                         }
                      }
                   }
                }
             }
          }
       }

       if(this.o != null) {
          this.Q = this.I + 10000;
       }

       if(this.o != null) {
          net.minecraft.client.c.MenuBackground var81 = this.o;

          while(Mouse.next()) {
             var81.f();
          }

          while(Keyboard.next()) {
             var81.g();
          }

          if(this.o != null) {
             this.o.f_();
          }
       }

       if(this.d != null) {
          this.d.E = this.w.u;
          if(!this.l) {
             this.q.a();
          }

          if(!this.l) {
             this.e.e();
          }

          if(!this.l) {
             this.d.c();
          }

          if(!this.l) {
             this.d.f();
          }

          if(!this.l) {
             this.d.k((int)this.f.h, (int)this.f.i, (int)this.f.j);
          }

          if(!this.l) {
             this.g.a();
          }
       }

   }

   public final void a(int var1, int var2, int var3, int var4) {
      this.a((net.minecraft.a.a.WorldHandler)null);
      System.gc();
      String var5 = this.h != null?this.h.b:"anonymous";
      System.out.printf("Setting user %s\n", new Object[]{var5});
      net.minecraft.a.a.c.LevelGenerator var6;
      (var6 = new net.minecraft.a.a.c.LevelGenerator(this.p)).a = var3 == 1;
      var6.b = var3 == 2;
      var6.c = var3 == 3;
      var6.d = var4;
      var3 = var1 = 128 << var1;
      short var8 = 64;
      if(var2 == 1) {
         var1 /= 2;
         var3 <<= 1;
      } else if(var2 == 2) {
         var3 = var1 /= 2;
         var8 = 256;
      }

      net.minecraft.a.a.WorldHandler var7 = var6.a(var5, var1, var3, var8);
      this.a(var7);
   }

   public final void a(net.minecraft.a.a.WorldHandler var1) {
      if(this.d != null) {
         this.d.k();
      }

      this.d = var1;
      if(var1 != null) {
         var1.a();
         this.a.a(var1);
         this.f = (net.minecraft.client.g.LocalPlayer)var1.b(net.minecraft.client.g.LocalPlayer.class);
         var1.y = this.f;
         if(this.f == null) {
            this.f = new net.minecraft.client.g.LocalPlayer(this, var1, this.h);
            this.f.j();
            if(var1 != null) {
               var1.a((net.minecraft.a.c.EntityGeneric)this.f);
               var1.y = this.f;
            }
         }

         if(this.f != null) {
            this.f.a = new net.minecraft.client.g.LocalPlayerOptions(this.w);
            this.a.a((net.minecraft.a.c.e.EntityPlayer)this.f);
         }

         if(this.e != null) {
            this.e.a(var1);
         }

         if(this.g != null) {
            this.g.a(var1);
         }

         this.O.d = 0;
         this.P.d = 0;
         int var4 = this.m.a("/water.png");
         if(var1.m == Block.WaterBlock.at) {
            this.O.d = var4;
         } else {
            this.P.d = var4;
         }
      }

      System.gc();
   }
}
