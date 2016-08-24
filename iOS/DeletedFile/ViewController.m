//
//  ViewController.m
//  Audioplayer
//
//  Created by Toobler on 04/03/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//
#import "TBMoods.h"
#import "NSAvailableMoods.h"
#import "ViewController.h"
#import "MediaPlayer/MediaPlayer.h"
#import <MediaToolbox/MediaToolbox.h>
#import <QuartzCore/QuartzCore.h>


#define IDIOM    UI_USER_INTERFACE_IDIOM()
#define IPAD     UIUserInterfaceIdiomPad
NSString *flag;
float MuteValue;
@interface ViewController ()


@end

@implementation ViewController
@synthesize animateView;
AVAudioPlayer *player;
AVAudioPlayer *player2;
MPMoviePlayerController *moviePlayer;



NSTimer *sliderTimer;


bool player1_running = false;
bool player2_running= false;

bool notification = false;
bool played = false;

bool movie_player_value = false;

BOOL initialLoading =true;


MPMediaItem *playingItem;

- (void)viewDidLoad
{
 
//*** To set MoodLabel While Loading from the TableView ***//
    
    TBMoods *MoodName=_selectedMood;
   // NSLog(@"%@", MoodName.moodDescription);
    MixMoodLabel.text=MoodName.moodDescription;
    
    
//*** To set MoodImage While Loading from the TableView ***//
    
    
    
     //NSLog(@"%@", MoodName.selectedFlag);
    if ([MoodName.selectedFlag isEqualToString:@"0"]) {
        
      [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"forest.png"]]];
        
        
    }else if ([MoodName.selectedFlag isEqualToString:@"1"])
        
    {
        [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"rain.png"]]];

    }else if ([MoodName.selectedFlag isEqualToString:@"2"])
    {
        [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"beach.png"]]];
    }
    
    else if ([MoodName.selectedFlag isEqualToString:@"3"])
    {
        [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"waterflow.png"]]];
        
    
    }
 
    
    flag=@"1";
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    
    
    //***To set View Corner Curve***//
    AddButtonView.layer.cornerRadius = 3;
    AddButtonView.layer.masksToBounds = YES;
    
    CtrlFirstView.layer.cornerRadius = 3;
    CtrlFirstView.layer.masksToBounds = YES;
    
    CtrlSecondView.layer.cornerRadius = 3;
    CtrlSecondView.layer.masksToBounds = YES;
    
    CtrlThirdView.layer.cornerRadius = 3;
    CtrlThirdView.layer.masksToBounds = YES;
    
    
 //***To set ThumbImage for Slider Button***//
    
    [ProgressBar setThumbImage: [UIImage imageNamed:@"blank.png"] forState:UIControlStateNormal];
    [ProgressBar setMaximumTrackImage:[UIImage imageNamed:@"player_slider_bg.png" ] forState:UIControlStateNormal];
    [ProgressBar setMinimumTrackImage:[UIImage imageNamed:@"player_slider_bg-progress.png"] forState:UIControlStateNormal];
        
    [volumeSlider setThumbImage: [UIImage imageNamed:@"mix_slider_handle_1x.png"] forState:UIControlStateNormal];
    [volumeSlider setMaximumTrackImage:[UIImage imageNamed:@"mix_slider_bg.png" ] forState:UIControlStateNormal];
    [volumeSlider setMinimumTrackImage:[UIImage imageNamed:@"mix_slider_bg_progress.png"] forState:UIControlStateNormal];
    [volumeSlider2 setThumbImage: [UIImage imageNamed:@"mix_slider_handle_1x.png"] forState:UIControlStateNormal];
    [volumeSlider2 setMaximumTrackImage:[UIImage imageNamed:@"mix_slider_bg.png" ] forState:UIControlStateNormal];
    [volumeSlider2 setMinimumTrackImage:[UIImage imageNamed:@"mix_slider_bg_progress.png"] forState:UIControlStateNormal];
    
    music.image = [UIImage imageNamed:@"mix_song.png"];
    mixer.image = [UIImage imageNamed:@"mix_mixer.png"];
    logo =[[UIImageView alloc] initWithFrame:CGRectMake(114,33,85,36)];
    logo.image=[UIImage imageNamed:@"logo.png"];
    [self.view addSubview:logo];
    [mute setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
    [mute1 setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
    
    
    NSMutableArray *moodsArray=[[NSMutableArray alloc]init];
    moodsArray=[[NSAvailableMoods getInstance]getAvailableMoods];
    //NSLog(@"%@",moodsArray);
    
//    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget: self action:@selector(handleSingleTap:)];
//    singleTap.numberOfTapsRequired = 1;
//    [aboveView addGestureRecognizer:singleTap];
    [super viewDidLoad];
    [self player2_play:nil];
   
}


-(void)viewWillAppear:(BOOL)animated
{
    
    [super viewWillAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)viewDidAppear:(BOOL)animated{
    
    CGRect Frame;
    if (self.view.bounds.size.height<568) {
        Frame=CGRectMake(4, 220, 312, 202);
    }
    else{
        Frame=CGRectMake(4, 264, 312, 202);
        
    }
    
    [UIView animateWithDuration:.4 animations:^{
       [CtrlThirdView setFrame:Frame];
    } completion:^(BOOL finished) {
  
    }];
   
    
    if (initialLoading) {
         [self openfolder:nil];
        initialLoading = false;
    }
   
}

/**
 *  @author Rohith
 *  HANDLE THE PLAY FUNCTION FOR PLAYER1
 */

- (IBAction)play:(id)sender {
    /*
     * Checking there is any songs available in the device
     * Else alert there is no media files in device
     */
    
    if(playingItem){

                   NSString *songTitle = [playingItem valueForProperty: MPMediaItemPropertyTitle];
        
                   NSString * artist  = [playingItem valueForProperty:MPMediaItemPropertyArtist];
        /*
         * Checking the player1 runnin status and if no status then create new player and assingn
         * new song else playing the song
         * Setting for on the paused condition the song length to be found.
         */
        if(!player1_running){
            
            player = [[AVAudioPlayer alloc] initWithContentsOfURL:[playingItem valueForProperty: MPMediaItemPropertyAssetURL] error:nil];
            
            
            player1_running =     true;
            
            
            /* Getting the current song duraion */
            
            ProgressBar.maximumValue = player.duration;
            [ProgressBar addTarget:self action:@selector(sliderChanged:) forControlEvents:UIControlEventValueChanged];
            [player prepareToPlay];
            
            /* Setting the player volume */
            if (volumeSlider.value <=0.0) {
                //[player setVolume:0.0];
                [mute setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];
                
            }else
                
            {
                [mute setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
                
                
            }

            player.volume = volumeSlider.value;
            [player setDelegate:self];
        }
        /* Setting the slider value changes by calling the update slider method */
        
        sliderTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateSlider) userInfo:nil repeats:YES];

        /*
         * Checking whether the player is already on or not by checking the "played"
         * If the player is on then setting the "played" value to false getting the
         * Volumeslider value and pausing the player.
         * Else player is set to play the song.
         */
        
        if(played){
            played = false;
            UIImage *buttonImage = [UIImage imageNamed:@"icon_Play.png"];
            [play_pause_Button setBackgroundImage:buttonImage forState:UIControlStateNormal];
         //  [controllView addSubview:play_pause_Button];
            [player setCurrentTime:ProgressBar.value];
            [player pause];
            [player prepareToPlay];
            
        }else{
            played = true;
            UIImage *buttonImage = [UIImage imageNamed:@"icon_Pause.png"];
            [play_pause_Button setBackgroundImage:buttonImage forState:UIControlStateNormal];
          // [controllView addSubview:play_pause_Button];
            [player setCurrentTime:ProgressBar.value];
            [player play];
        }
        
        /* 
         * Calculating the time of the song currently playing and showing the increment
         * value according to the player play the song.
         */
        float cTime =ProgressBar.maximumValue;
        int minutes = floor(cTime/60);
        int seconds = trunc(cTime - minutes * 60);
        [endText setText:[NSString stringWithFormat:@"%d:%02d", minutes, seconds]];
        
        /*
         * Setting the song title and the artist name from the arrays and set text values
         */
        [playing_label setText:songTitle];
       
        if(artist == (NSString *)[NSNull null]){
            artist = @"Unknown Artist";
        }
        [artist_Label setText:artist];
        
        if( ProgressBar.maximumValue == 0){
            [self nextButtonClick:sender];
        }
        
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"NO FILES"
                                                        message:@"No Files Found in the Music Library" delegate:self cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

//...To Mute the Song Sound...//
- (IBAction)SongMute:(id)sender
{
    static BOOL muted = NO;
    if (muted) {
        
        [mute setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];

        player.volume =MuteValue;
        //[player setVolume:1.0];
    } else {
        [player setVolume:0.0];
        [mute setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];

            }
    muted = !muted;
}


//...To Mute the Mixer Sound...//
- (IBAction)MixerMute:(id)sender
{
    static BOOL muted = NO;
    if (muted) {
        [mute1 setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];

        player2.volume =MuteValue;
       // [player2 setVolume:1.0];
    } else {
        [player2 setVolume:0.0];
        [mute1 setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];

    }
    muted = !muted;
}




/*
 *  @author Rohith
 *
 * HANDLE THE SLIDER CHANGES ACCORDING
 * THE PLAYER 1 PLAYS THE SONG
 */

- (void)updateSlider {
    if(playingItem){
        // Update the slider about the music time
        ProgressBar.value = player.currentTime;
        float cTime =player.currentTime;
        int minutes = floor(cTime/60);
        int seconds = trunc(cTime - minutes * 60);
        [startText setText:[NSString stringWithFormat:@"%d:%02d", minutes, seconds]];
    }
}

- (IBAction)sliderChanged:(UISlider *)sender {
    if(playingItem){
        // Fast skip the music when user scroll the UISlider
        [player stop];
        [player setCurrentTime:ProgressBar.value];
        [player prepareToPlay];
        if(played){
            [player play];
        }
    }
}

- (IBAction)volumeChange:(id)sender {
    if (volumeSlider.value <=0.0) {
        //[player setVolume:0.0];
        [mute setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];
        
    }else
        
    {
        [mute setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
        
        
    }
    MuteValue =volumeSlider.value;
    player.volume = volumeSlider.value;
    
   // NSLog(@"%@", volumeSlider);
}

- (IBAction)Backward:(id)sender {
    if(playingItem){
    [self fetchNear:false];
    sliderTimer = 0;
    ProgressBar.value = 0;
    player1_running =false;
    [player stop];
    [self play:nil];
    }
}

- (IBAction)nextButtonClick:(id)sender {
    
    if(playingItem){
        
    [self fetchNear:true];
    sliderTimer = 0;
    ProgressBar.value = 0;
    player1_running =false;
    [player stop];
    [self play:nil];
    }
}



- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)play successfully:(BOOL)flag {
    /*
     * If player2 is finished then
     * it is called again for playing for
     * didnt stop the mood music
     */
    if(playingItem){
        /*
         * Checking the player stopped is player1 or player2
         */
        if( play == player2){
            [player2 play];
        }else{
            // For player1
            // Setting the Slider variation after the player stops
            if (flag) {
                [sliderTimer invalidate];
            }
            // If the last listed file get played and completed
            // Again reversed to the first file in the list
            // If the music completed it goes to 1st song
            
            [self fetchNear:true];
            player1_running = false;
            played = false;
            ProgressBar.value = 0;
            [self play:nil];
        }}
}

/*
 *  @author Rohith
 *
 *  HANDLED ON VOLUME CHANGE
 */

- (IBAction)volumeChange2:(id)sender {
    if (volumeSlider2.value <=0.0) {
                //[player2 setVolume:0.0];
        [mute1 setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];

    }else
    
   {
       [mute1 setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
       

    }
    
     MuteValue =volumeSlider2.value;
    player2.volume = volumeSlider2.value;
    
    //NSLog(@"%@", volumeSlider2);
}

/*
 * Function Handles the playing of player2 for the mood to set
 * Checking the player is already running by checking the "player2_running" value
 * Else plays the mood song
 */
- (IBAction)player2_play:(id)sender {

    if(player2_running){
        
        
         // For pausing the player2 setting up the teststring to nil, movie_player_value to false
         // And setting the buttons to the previous style.
        
        movie_player_value = false;
        [moviePlayer.view setFrame:CGRectMake(0, 0, 0, 0)];
        
        // Setting the movieplayer content to nil and stops the movie player
        moviePlayer.contentURL = nil;
        [moviePlayer stop];
        
        // stopping the mood song player
        [player2 stop];
        
        // setting the playing text and image to nil
        [playingimage setImage:nil];
        [playingText setText:nil ];
        player2_running = false;
    }
    
    
    if(!player2_running)
    {
        
        
         // For starting or restarting the  mood player , stopping the movie player for start another video
        
        player2_running = true;
        [moviePlayer stop];
        
        {
            
             // else Setting the images and texts for rain
             
            [playingimage setImage:[UIImage imageNamed:@"rain"]];
            [playingText setText:[NSString stringWithFormat:@"Rain & Thunder"] ];
            [buttonRain setBackgroundImage:[UIImage imageNamed:@"rain_pause"] forState:UIControlStateNormal];
            [buttonnature setBackgroundImage:[UIImage imageNamed:@"forest"] forState:UIControlStateNormal];
            [buttonSea setBackgroundImage:[UIImage imageNamed:@"sea"] forState:UIControlStateNormal];
        }
        
       
         // Setting the file path which the movie player file to be stored
        //Starting the movie player by setting the new resource
         // Starting the mood player also by setting the new resource
        
        
        NSURL *songURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:_selectedMood.moodSound  ofType:@"mp3"]];
        player2 = [[AVAudioPlayer alloc] initWithContentsOfURL:songURL error:nil];
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryPlayback error:nil];
        [[AVAudioSession sharedInstance] setActive: YES error: nil];
        [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
        player2.delegate=self;
        if (volumeSlider2.value <=0.0) {
            //[player setVolume:0.0];
            [mute1 setImage:[UIImage imageNamed:@"mix_mute.png"] forState:UIControlStateNormal];
            
        }else
            
        {
            [mute1 setImage:[UIImage imageNamed:@"mix_mute@2x.png"] forState:UIControlStateNormal];
            
            
        }

        player2.volume = volumeSlider2.value;
        [player2 play];
    }
    
}



- (IBAction)openfolder:(id)sender {
    MPMediaPickerController *picker = [[MPMediaPickerController alloc] initWithMediaTypes:MPMediaTypeAnyAudio];
    [picker setDelegate:self];
    [picker setAllowsPickingMultipleItems:NO];
    [picker setPrompt:NSLocalizedString(@"Add songs to play","Prompt in media item picker")];
    
    @try {
        [picker loadView]; // Will throw an exception in iOS simulator
        [self presentViewController:picker animated:YES completion:nil];
    }
    @catch (NSException *exception) {
        [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Oops!",@"Error title")
                                    message:NSLocalizedString(@"The music library is not available.",@"Error message when MPMediaPickerController fails to load")
                                   delegate:nil
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil] show];
    }
}

/**
 *  @author Rohith
 *  AUTOMATICALLY HANDLED WHEN WE PICK ANY FILE FROM THE LIST
 *
 * It will handle when ever any of the songs from the list selected
 * and it will checkout the positon and array index of the song selected
 */

- (void)mediaPicker: (MPMediaPickerController *)mediaPicker didPickMediaItems:
(MPMediaItemCollection *)mediaItemCollection
{
    [self dismissViewControllerAnimated:YES completion:nil];
    playingItem = [[mediaItemCollection items] objectAtIndex:0];
    
    /*
     * Setting the player1 running status to false and stop the player1
     */
    
    player1_running =false;
    [player stop];
    sliderTimer = 0;
    ProgressBar.value = 0;
    played = false;
    [self play:nil];
    
}

/*
 * Handling the Cancellation the media list
 */
- (void) mediaPickerDidCancel: (MPMediaPickerController *) mediaPicker
{
    [self dismissViewControllerAnimated:YES completion:nil];
}



-(void)fetchNear:(BOOL)next{
    if(playingItem)
    {
        MPMediaQuery *everything = [[MPMediaQuery alloc] init];
        NSArray *itemsFromGenericQuery = [everything items];
        everything = nil;
    if ([itemsFromGenericQuery containsObject:playingItem]) {
        int  i =  [itemsFromGenericQuery indexOfObject:playingItem];
        if(next){
            // fetch Next Song
            if((i+1)<[itemsFromGenericQuery count]){
                playingItem =[itemsFromGenericQuery objectAtIndex:i+1];
            }else if([itemsFromGenericQuery count]>0){
                playingItem =[itemsFromGenericQuery objectAtIndex:0];
            }
            else{
                playingItem =nil;
            }
        }else{
            // fetch Previous Song
            if((i-1)>=0){
                playingItem =[itemsFromGenericQuery objectAtIndex:i-1];
            }else if([itemsFromGenericQuery count]>0){
                playingItem =[itemsFromGenericQuery objectAtIndex:[itemsFromGenericQuery count]-1];
            }
            else{
                playingItem =nil;
            }
        }
    }
    }

    
    if(played){
        played = false;
    }else{
        played = true;
    }
}


-(IBAction)handlingMoods:(id)sender
{
    
    _selectedMood=[[[NSAvailableMoods getInstance]getAvailableMoods]objectAtIndex:[sender tag]];
     [self player2_play:nil];
    
   
    NSLog(@"%@", _selectedMood);

    if ([[NSString stringWithFormat:@"%ld",(long)[sender tag]]isEqualToString:@"0"]) {
        MixMoodLabel.text=@"Mix Your Song with Nature Mood";
          [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"rain.png"]]];


        NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                        [UIImage imageNamed:@"forest@2x.png"],nil];
        
        [animateView setImages:animationArray:animationArray];

        
    }
    if ([[NSString stringWithFormat:@"%ld",(long)[sender tag]]isEqualToString:@"1"]) {
        MixMoodLabel.text=@"Mix Your Song with Rain Mood";

    [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"rain.png"]]];
        
        NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                        [UIImage imageNamed:@"rain.png"],nil];
        
       [animateView setImages:animationArray:animationArray];
        

        
    }

    if ([[NSString stringWithFormat:@"%ld",(long)[sender tag]]isEqualToString:@"2"]) {
        MixMoodLabel.text=@"Mix Your Song with Wave Mood";

     [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"beach.png"]]];
        
        NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                        [UIImage imageNamed:@"beach.png"],nil];
        
        [animateView setImages:animationArray:animationArray];
        

        
    }
    if ([[NSString stringWithFormat:@"%ld",(long)[sender tag]]isEqualToString:@"3"]) {
        MixMoodLabel.text=@"Mix Your Song with WaterFlow Mood";

       [animateView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"waterflow.png"]]];
        
        NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                        [UIImage imageNamed:@"waterflow.png"],nil];
        
        [animateView setImages:animationArray:animationArray];
        

        
    
    }
   

}

-(IBAction)animationButton:(id)sender
{
    
    if ([flag isEqualToString:@"1"]) {
        flag=@"0";
        
        [UIView animateWithDuration:0.4 animations:^{
            controllView.hidden =YES;
            //LogoImage.hidden =YES;
            [logo setFrame:CGRectMake(114,568,85,36)];
            AddButtonView.hidden =YES;
            //MixMoodLabel.hidden =YES;
            [MixMoodLabel setFrame:CGRectMake(0, 568, 320, 33)];
            //AddButton.hidden =YES;
            [AddButton setFrame:CGRectMake(280, 568, 40, 40)];
            //CtrlThirdView.hidden=YES;
            [CtrlThirdView setFrame:CGRectMake(4, 568, 312, 202)];
            //CtrlSecondView.hidden=YES;
            [CtrlSecondView setFrame:CGRectMake(0, 571, 320, 52)];
            //CtrlFirstView.hidden=YES;
            [CtrlFirstView setFrame:CGRectMake(4, 568, 312, 104)];
            
            TBMoods *MoodName=_selectedMood;
            NSLog(@"%@", MoodName.selectedFlag);
            
            if ([MoodName.selectedFlag isEqualToString:@"0"]) {
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"forest@2x.png"],
                                                //[UIImage imageNamed:@"a-path-through-the-forest-and-a-creek-through-the-neanderthal-valley.jpg"],
                                                //[UIImage imageNamed:@"DSC_1465.jpg"],
                                                nil];
                
                //TBLAnimateView *animate = [[TBLAnimateView alloc]init];
                
                [animateView setImages:animationArray:animationArray];
                
                
            }
            else   if ([MoodName.selectedFlag isEqualToString:@"1"]) {
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"rain.png"],
                                                //[UIImage imageNamed:@"9604-evening-sea-iphone-hd-wallpaper_640x960.jpg"],
                                                //[UIImage imageNamed:@"rain.jpg"],
                                                //[UIImage imageNamed:@"rain-on-a-sunny-day.jpg"],
                                                //[UIImage imageNamed:@"rain_photos_03.jpg"],
                                                nil];
                //TBLAnimateView *animate = [[TBLAnimateView alloc]init];
                
                [animateView setImages:animationArray:animationArray];
                
            }
            
            else if ([MoodName.selectedFlag isEqualToString:@"2"]){
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"beach.png"],
                                                //[UIImage imageNamed:@"image1.jpg"],
                                                //[UIImage imageNamed:@"image2.jpg"],
                                                //[UIImage imageNamed:@"d8f417_e888d7a26d5d436dbf83578681d28590.jpg_srz_325_371_85_22_0.50_1.20_0.00_jpg_srz"],
                                                nil];
                
                //TBLAnimateView *animate = [[TBLAnimateView alloc]init];
                
                [animateView setImages:animationArray:animationArray];
                
            }
            
            else if ([MoodName.selectedFlag isEqualToString:@"3"]){
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"waterflow.png"],
                                                //[UIImage imageNamed:@"forest-view-picture-7.jpg"],
                                                //[UIImage imageNamed:@"glen-etive-1280-800-1590.jpg"],
                                                nil];
                
                //TBLAnimateView *animate = [[TBLAnimateView alloc]init];
                
                [animateView setImages:animationArray:animationArray];
                
            }

            
        } completion:^(BOOL finished) {
            
            
            
        }];
        
        
    }else{
        
        [UIView animateWithDuration:0.4 animations:^{
            controllView.hidden =NO;
            // LogoImage.hidden =NO;
            [logo setFrame:CGRectMake(114,33,85,36)];
            AddButtonView.hidden =NO;
            //MixMoodLabel.hidden =NO;
            [MixMoodLabel setFrame:CGRectMake(0, 65, 320, 33)];
            //AddButton.hidden =NO;
            [AddButton setFrame:CGRectMake(280, 27, 40, 40)];
            //CtrlThirdView.hidden=NO;
            if (self.view.bounds.size.height<568) {
                [CtrlThirdView setFrame:CGRectMake(4, 220, 312, 202)];
            }
            else{
                [CtrlThirdView setFrame:CGRectMake(4, 264, 312, 202)];
            }
            
            // CtrlSecondView.hidden=NO;
            if (self.view.bounds.size.height<568) {
                [CtrlSecondView setFrame:CGRectMake(0,self.view.bounds.size.height-52, 320, 52)];
                
            }
            else{
                [CtrlSecondView setFrame:CGRectMake(0, 516, 320, 202)];
                
            }
            //CtrlFirstView.hidden=NO;
            [CtrlFirstView setFrame:CGRectMake(4, 110, 312, 104)];
            
            TBMoods *MoodName=_selectedMood;
            NSLog(@"%@", MoodName.selectedFlag);
            if ([MoodName.selectedFlag isEqualToString:@"0"]) {
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"forest@2x.png"],nil];
                
                [animateView setImages:animationArray:animationArray];
            }
            else   if ([MoodName.selectedFlag isEqualToString:@"1"]) {
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"rain.png"],nil];
                
                [animateView setImages:animationArray:animationArray];
                
            }
            
            else if ([MoodName.selectedFlag isEqualToString:@"2"]){
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"beach.png"],nil];
                
                [animateView setImages:animationArray:animationArray];
                
            }
            
            else if ([MoodName.selectedFlag isEqualToString:@"3"]){
                
                NSMutableArray *animationArray=[NSMutableArray arrayWithObjects:
                                                [UIImage imageNamed:@"waterflow.png"],
                                                nil];
                
                [animateView setImages:animationArray:animationArray];
                
            }

            
        } completion:^(BOOL finished) {
            
            
            
            
        }];
        
        flag=@"1";
        
    }
    
   }


@end
