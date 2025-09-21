package com.google.android.exoplayer2.source.sabr.parser;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.source.sabr.parser.exceptions.MediaSegmentMismatchError;
import com.google.android.exoplayer2.source.sabr.parser.parts.SabrPart;
import com.google.android.exoplayer2.source.sabr.parser.processor.ProcessMediaHeaderResult;
import com.google.android.exoplayer2.source.sabr.parser.processor.SabrProcessor;
import com.google.android.exoplayer2.source.sabr.parser.ump.UMPDecoder;
import com.google.android.exoplayer2.source.sabr.parser.ump.UMPPart;
import com.google.android.exoplayer2.source.sabr.parser.ump.UMPPartId;
import com.google.android.exoplayer2.source.sabr.protos.videostreaming.ClientAbrState;
import com.google.android.exoplayer2.source.sabr.protos.videostreaming.MediaHeader;
import com.google.protobuf.InvalidProtocolBufferException;
import com.liskovsoft.sharedutils.mylogger.Log;

public class SabrStreamParser {
    private static final String TAG = SabrStreamParser.class.getSimpleName();
    private final UMPDecoder decoder;
    private final SabrProcessor processor;
    private int sqMismatchForwardCount;
    private int sqMismatchBacktrackCount;
    private final int[] KNOWN_PARTS = {
            UMPPartId.MEDIA_HEADER,
            UMPPartId.MEDIA,
            UMPPartId.MEDIA_END,
            UMPPartId.STREAM_PROTECTION_STATUS,
            UMPPartId.SABR_REDIRECT,
            UMPPartId.FORMAT_INITIALIZATION_METADATA,
            UMPPartId.NEXT_REQUEST_POLICY,
            UMPPartId.LIVE_METADATA,
            UMPPartId.SABR_SEEK,
            UMPPartId.SABR_ERROR,
            UMPPartId.SABR_CONTEXT_UPDATE,
            UMPPartId.SABR_CONTEXT_SENDING_POLICY,
            UMPPartId.RELOAD_PLAYER_RESPONSE
    };

    public SabrStreamParser(@NonNull ExtractorInput extractorInput) {
        decoder = new UMPDecoder(extractorInput);
        processor = new SabrProcessor();
    }

    public SabrPart parse() {
        SabrPart result = null;

        while (true) {
            UMPPart part = nextKnownUMPPart();

            if (part == null) {
                break;
            }

            result = parsePart(part);

            if (result != null) {
                break;
            }
        }

        return result;
    }

    private SabrPart parsePart(UMPPart part) {
        switch (part.partId) {
            case UMPPartId.MEDIA_HEADER:
                return processMediaHeader(part);
            case UMPPartId.MEDIA:
                return processMedia(part);
            case UMPPartId.MEDIA_END:
                return processMediaEnd(part);
            case UMPPartId.STREAM_PROTECTION_STATUS:
                return processStreamProtectionStatus(part);
            case UMPPartId.SABR_REDIRECT:
                return processSabrRedirect(part);
            case UMPPartId.FORMAT_INITIALIZATION_METADATA:
                return processFormatInitializationMetadata(part);
            case UMPPartId.NEXT_REQUEST_POLICY:
                return processNextRequestPolicy(part);
            case UMPPartId.LIVE_METADATA:
                return processLiveMetadata(part);
            case UMPPartId.SABR_SEEK:
                return processSabrSeek(part);
            case UMPPartId.SABR_ERROR:
                return processSabrError(part);
            case UMPPartId.SABR_CONTEXT_UPDATE:
                return processSabrContextUpdate(part);
            case UMPPartId.SABR_CONTEXT_SENDING_POLICY:
                return processSabrContextSendingPolicy(part);
            case UMPPartId.RELOAD_PLAYER_RESPONSE:
                return processReloadPlayerResponse(part);
        }

        return null;
    }

    private SabrPart processMediaHeader(UMPPart part) {
        MediaHeader mediaHeader;

        try {
            mediaHeader = MediaHeader.parseFrom(part.data);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalStateException(e);
        }

        try {
            ProcessMediaHeaderResult result = processor.processMediaHeader(mediaHeader);

            return result.sabrPart;
        } catch (MediaSegmentMismatchError e) {
            // For livestreams, the server may not know the exact segment for a given player time.
            // For segments near stream head, it estimates using segment duration, which can cause off-by-one segment mismatches.
            // If a segment is much longer or shorter than expected, the server may return a segment ahead or behind.
            // In such cases, retry with an adjusted player time to resync.
            if (processor.isLive() && e.receivedSequenceNumber == e.expectedSequenceNumber - 1) {
                // The segment before the previous segment was possibly longer than expected.
                // Move the player time forward to try to adjust for this.
                ClientAbrState state = processor.getClientAbrState().toBuilder()
                        .setPlayerTimeMs(processor.getClientAbrState().getPlayerTimeMs() + processor.getLiveSegmentTargetDurationToleranceMs())
                        .build();
                processor.setClientAbrState(state);
                sqMismatchForwardCount += 1;
                return null;
            } else if (processor.isLive() && e.receivedSequenceNumber == e.expectedSequenceNumber + 2) {
                // The previous segment was possibly shorter than expected
                // Move the player time backwards to try to adjust for this.
                ClientAbrState state = processor.getClientAbrState().toBuilder()
                        .setPlayerTimeMs(Math.max(0, processor.getClientAbrState().getPlayerTimeMs() - processor.getLiveSegmentTargetDurationToleranceMs()))
                        .build();
                processor.setClientAbrState(state);
                sqMismatchBacktrackCount += 1;
                return null;
            }

            throw e;
        }
    }

    private SabrPart processMedia(UMPPart part) {
        return null;
    }

    private SabrPart processMediaEnd(UMPPart part) {
        return null;
    }

    private SabrPart processStreamProtectionStatus(UMPPart part) {
        return null;
    }

    private SabrPart processSabrRedirect(UMPPart part) {
        return null;
    }

    private SabrPart processFormatInitializationMetadata(UMPPart part) {
        return null;
    }

    private SabrPart processNextRequestPolicy(UMPPart part) {
        return null;
    }

    private SabrPart processLiveMetadata(UMPPart part) {
        return null;
    }

    private SabrPart processSabrSeek(UMPPart part) {
        return null;
    }

    private SabrPart processSabrError(UMPPart part) {
        return null;
    }

    private SabrPart processSabrContextUpdate(UMPPart part) {
        return null;
    }

    private SabrPart processSabrContextSendingPolicy(UMPPart part) {
        return null;
    }

    private SabrPart processReloadPlayerResponse(UMPPart part) {
        return null;
    }

    public static boolean contains(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }

    private UMPPart nextKnownUMPPart() {
        UMPPart part;

        while (true) {
            part = decoder.decode();

            if (part == null) {
                break;
            }

            if (contains(KNOWN_PARTS, part.partId)) {
                break;
            } else {
                Log.d(TAG, "Unknown part encountered: %s", part.partId);
            }
        }

        return part;
    }
}
